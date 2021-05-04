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

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Slf4j
public class TopicReferringStatistics {

    public static void main(String[] args) {
        final Serde<String> serdeString = Serdes.String();
        final Serde<Long> serdeLong = Serdes.Long();
        final Serde<String[]> serdeStringArray = createJsonPOJOSerdes(String[].class);
        final Serde<CdcRecord> serdeCdcRecord = createJsonPOJOSerdes(CdcRecord.class);
        final Serde<GenericItem> serdeGenericItem = createJsonPOJOSerdes(GenericItem.class);
        final Serde<CategoriesAndOccurrence> serdeCategoriesAndOccurrence = createJsonPOJOSerdes(CategoriesAndOccurrence.class);
        final Serde<KeywordMatch> serdeKeywordMatch = new SpecificAvroSerde<>();
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://localhost:8081");
        serdeKeywordMatch.configure(serdeConfig, true);

        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "topic-referring-statistics-2");
        props.put("schema.registry.url", "http://localhost:8081");

        StreamsBuilder builder = new StreamsBuilder();

        /* Declare keyword table */
        KTable<String, String[]> keywordTable = builder.stream("postgres-server.public.keywords", Consumed.with(serdeString, serdeCdcRecord))
                .flatMap(TopicReferringStatistics::flatMapPostgresCDCRecord)
                .toTable(Materialized.with(serdeString, serdeStringArray));

        /* Declare comment source */
        KStream<String, CategoriesAndOccurrence> wordMatches = builder.stream("hacker-news-comment", Consumed.with(serdeString, serdeGenericItem))
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
                .to("keyword-matches");

        wordMatches.to("result", Produced.with(serdeString, serdeCategoriesAndOccurrence));

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

    public static Iterable<KeyValue<? extends String, ? extends String[]>> flatMapPostgresCDCRecord(String s, CdcRecord cdcRecord) {
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
                return Collections.singletonList(new KeyValue<>(after.getKeyword(), after.getCategory()));
            case Update:
                assert before != null;
                assert after != null;
                List<KeyValue<? extends String, ? extends String[]>> updates = new ArrayList<>();
                if(!before.getKeyword().equals(after.getKeyword())) {
                    updates.add(new KeyValue<>(before.getKeyword(), null));
                    updates.add(new KeyValue<>(after.getKeyword(), after.getCategory()));
                } else if(!Arrays.equals(before.getCategory(), after.getCategory())) {
                    assert before.getKeyword().equals(after.getKeyword());
                    updates.add(new KeyValue<>(after.getKeyword(), after.getCategory()));
                }
                return updates;
            case Delete:
                assert before != null;
                return Collections.singletonList(new KeyValue<>(before.getKeyword(), null));
        }
        throw new AssertionError();
    }

    public static Iterable<KeyValue<String, Long>> mapGenericItemTextToWordIdPairs(String key, GenericItem value) {
        if(value == null || value.getText() == null)
            return Collections.emptyList();
        return stream(value.getText().toLowerCase(Locale.getDefault()).split("\\W+"))
                .map((word) -> new KeyValue<String, Long>(word, value.getId()))
                .collect(Collectors.toList());
    }

    public static <T> Serde<T> createJsonPOJOSerdes(Class<T> targetClass) {
        JsonPOJOSerializer<T> serializer = new JsonPOJOSerializer<>();
        JsonPOJODeserializer<T> deserializer = new JsonPOJODeserializer<>(targetClass);
        return Serdes.serdeFrom(serializer, deserializer);
    }

}
