package org.example;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.example.domain.KeywordMatch;
import org.example.hackernews.GenericItem;
import org.example.keywords.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
public class TopicReferringStatistics {

    public static final String SCHEMA_REGISTRY_URL = "schema.registry.url";
    public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static final String COMMENT_SOURCE = "comment.source";
    public static final String KEYWORD_MATCHES_OUTPUT = "keyword.matches.output";
    public static final String APP_CONFIG_PREFIX = "APP_";

    public static void main(String[] args) {
        Properties properties = new Properties();
        loadPropertiesFromClasspath(properties, "/application.properties");
        loadPropertiesFromEnvironment(properties, APP_CONFIG_PREFIX);

        StringBuilder propertyString = new StringBuilder("Application Configuration");
        properties.entrySet().stream().sequential().forEach((item) -> {
            propertyString
                    .append("    ")
                    .append(System.lineSeparator())
                    .append(item.getKey())
                    .append("=")
                    .append(item.getValue());
        });
        log.info(propertyString.toString());

        final Serde<String> serdeString = Serdes.String();
        final Serde<Long> serdeLong = Serdes.Long();
        final Serde<Integer> serdeInt = Serdes.Integer();
        final Serde<String[]> serdeStringArray = createJsonPOJOSerdes(String[].class);
        final Serde<CdcRecord> serdeCdcRecord = createJsonPOJOSerdes(CdcRecord.class);
        final Serde<GenericItem> serdeGenericItem = createJsonPOJOSerdes(GenericItem.class);
        final Serde<KeywordAndCategories> serdeKAC = createJsonPOJOSerdes(KeywordAndCategories.class);
        final Serde<CategoriesAndOccurrence> serdeCategoriesAndOccurrence = createJsonPOJOSerdes(CategoriesAndOccurrence.class);
        final Serde<KeywordMatch> serdeKeywordMatch = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", properties.getProperty(SCHEMA_REGISTRY_URL));
        serdeKeywordMatch.configure(serdeConfig, true);

        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getProperty(BOOTSTRAP_SERVERS));
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "topic-referring-statistics-2");
        props.put("schema.registry.url", properties.getProperty(SCHEMA_REGISTRY_URL));

        String topicCommentSource = properties.getProperty(COMMENT_SOURCE);
        String topicKeywordMatchesResult = properties.getProperty(KEYWORD_MATCHES_OUTPUT);

        StreamsBuilder builder = new StreamsBuilder();

        /* Declare keyword table */
        KTable<String, String[]> keywordTable = builder.stream("postgres-server.public.keywords", Consumed.with(serdeString, serdeCdcRecord))
                // For some reason Postgres CDC Connector only know the primary key of the deleted record
                // instead of all fields when Delete event occurred.
                // We want the table to be KTable<Keyword, Categories>, but due to the above reason we can't do that.
                // Now we need to make it KTable<Integer, (Keyword, Categories)>, so the delete function will work properly.
                // After that we transform it back to what we want.
                .flatMap(TopicReferringStatistics::flatMapPostgresCDCRecord)
                .toTable(Materialized.with(serdeInt, serdeKAC))
                // The following code will ...
                // Transform KTable<Integer, (Keyword, Categories)>
                //      into KTable<Keyword, Categories>
                // The reason that we need to make things so complicated is probably due to the poorly designed database table :3
                // If we use the Keyword string as the primary key instead of a meaningless id number then we won't have to suffer right now.
                .groupBy((Integer integer, KeywordAndCategories kac) -> {
                    return new KeyValue<>(kac.getKeyword(), kac.getCategories());
                }, Grouped.with(serdeString, serdeStringArray)).reduce(
                        (aggValue, newValue) -> newValue,
                        (aggValue, oldValue) -> null
                );

        /* Declare comment source */
        KStream<String, CategoriesAndOccurrence> wordMatches = builder.stream(topicCommentSource, Consumed.with(serdeString, serdeGenericItem))
                // TODO: Implement some tests to ensure that the <word, hacker news item id> pair won't duplicate
                .flatMap(TopicReferringStatistics::mapGenericItemTextToWordIdPairs)
                .repartition(Repartitioned.with(serdeString, serdeLong))
                .join(keywordTable, (commentItemId, relatedTopics) -> new CategoriesAndOccurrence(commentItemId, relatedTopics));

        wordMatches.flatMap((String key, CategoriesAndOccurrence cao) -> {
            long id = cao.getOccurrenceId();
            return stream(cao.getCategories())
                    .map((category) -> KeyValue.pair(category, KeywordMatch.newBuilder()
                            .setKeyword(key)
                            .setHackerNewsItemId(id)
                            .setCategory(category)
                            .build()))
                    .collect(Collectors.toList());
        }).repartition(Repartitioned.with(serdeString, serdeKeywordMatch))
                .to(topicKeywordMatchesResult);

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        log.info(topology.describe().toString());

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    public static List<KeyValue<Integer, KeywordAndCategories>> flatMapPostgresCDCRecord(String s, CdcRecord cdcRecord) {
        if(cdcRecord == null){
            log.warn("Find a null CDC record with key: " +s);
            return Collections.emptyList();
        }

        CdcRecord.DataRecord before = cdcRecord.getBefore();
        CdcRecord.DataRecord after = cdcRecord.getAfter();
        switch(cdcRecord.getOp()) {
            case Create:
            case Read:
                assert after != null;
                return Collections.singletonList(new KeyValue<>(after.getId(), new KeywordAndCategories(after.getKeyword(), after.getCategory())));
            case Update:
                assert before != null;
                assert after != null;
                assert before.getId() == after.getId();
                List<KeyValue<Integer, KeywordAndCategories>> updates = new ArrayList<>();
                updates.add(new KeyValue<>(after.getId(), new KeywordAndCategories(after.getKeyword(), after.getCategory())));
                return updates;
            case Delete:
                assert before != null;
                return Collections.singletonList(new KeyValue<>(before.getId(), null));
        }
        throw new AssertionError();
    }

    public static Iterable<KeyValue<String, Long>> mapGenericItemTextToWordIdPairs(String key, GenericItem value) {
        if(value == null || value.getText() == null)
            return Collections.emptyList();
        return stream(value.getText().toLowerCase(Locale.getDefault()).split("\\W+"))
                .map((word) -> new KeyValue<String, Long>(word, value.getId()))
                .collect(Collectors.toSet());
    }

    public static <T> Serde<T> createJsonPOJOSerdes(Class<T> targetClass) {
        JsonPOJOSerializer<T> serializer = new JsonPOJOSerializer<>();
        JsonPOJODeserializer<T> deserializer = new JsonPOJODeserializer<>(targetClass);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    private static void loadPropertiesFromEnvironment(Properties props, String prefix) {
        Map<String, String> environments = System.getenv();
        for (String env : environments.keySet()) {
            if(env.startsWith(prefix))
                props.put(env.substring(prefix.length()).toLowerCase(Locale.ROOT).replace("_", "."), environments.get(env));
        }
    }

    private static void loadPropertiesFromClasspath(Properties props, String file) {
        try (InputStream kafkaConfiguration = TopicReferringStatistics.class.getResourceAsStream(file)) {
            props.load(kafkaConfiguration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
