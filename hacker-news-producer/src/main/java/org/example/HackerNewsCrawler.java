package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.hackernews.GenericItem;
import org.example.hackernews.ItemSerializer;
import org.example.hackernews.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class HackerNewsCrawler {
    /* Tired of vanilla java, I supposed to use some framework. */
    private static final Logger logger = LoggerFactory.getLogger(HackerNewsCrawler.class);
    private static final String configurationFileKafka = "/kafka-producer.properties";
    private static final String configurationFileApp = "/application-default.properties";
    private static final String kafkaTargetTopic = "application.kafka.target.topic";
    private static final String hackerNewsItemBegin = "application.hackernews.item.begin";
    private static final String hackerNewsItemOffset = "application.hackernews.item.offset";
    private static final String hackerNewsCrawInterval = "application.hackernews.craw.interval";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CONFIG_PREFIX_APPLICATION = "APP_";
    private static final String CONFIG_PREFIX_KAFKA_PRODUCER = "PRODUCER_";
    private static Properties appConfig;
    private static Properties producerConfig;

    public static void main(String[] args)
    {
        logger.info("Application started");
        appConfig = new Properties();
        producerConfig = new Properties();

        loadPropertiesFromClasspath(appConfig, configurationFileApp);
        loadPropertiesFromClasspath(producerConfig, configurationFileKafka);
        loadPropertiesFromEnvironment(appConfig, CONFIG_PREFIX_APPLICATION);
        loadPropertiesFromEnvironment(producerConfig, CONFIG_PREFIX_KAFKA_PRODUCER);

        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ItemSerializer.class);

        StringBuilder applicationConfigString = new StringBuilder("Application Configuration");
        appConfig.entrySet().stream().sequential().forEach((item) -> {
            applicationConfigString
                    .append(System.lineSeparator())
                    .append("    ")
                    .append(item.getKey())
                    .append("=")
                    .append(item.getValue());
        });
        logger.info(applicationConfigString.toString());

        StringBuilder producerConfigString = new StringBuilder("Producer Configuration");
        producerConfig.entrySet().stream().sequential().forEach((item) -> {
            producerConfigString
                    .append(System.lineSeparator())
                    .append("    ")
                    .append(item.getKey())
                    .append("=")
                    .append(item.getValue());
        });
        logger.info(producerConfigString.toString());

        Producer<String, GenericItem> producer = new KafkaProducer<>(producerConfig);
        OkHttpClient httpClient = new OkHttpClient();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown producer");
            producer.close();
        }));

        logger.info("Start sending requests");
        start(httpClient, producer);

        producer.flush();
    }

    private static void start(OkHttpClient client, Producer<String, GenericItem> producer) {
        final int start = Integer.parseInt(appConfig.getProperty(hackerNewsItemBegin));
        final int offset = Integer.parseInt(appConfig.getProperty(hackerNewsItemOffset));
        final int interval = Integer.parseInt(appConfig.getProperty(hackerNewsCrawInterval));
        final String topic = appConfig.getProperty(kafkaTargetTopic);
        for(int index = start; ; index += offset) {
            Optional<byte[]> body = retrieveNews(client, index);

            if(body.isPresent()) {
                try {
                    GenericItem item = objectMapper.readValue(body.get(), GenericItem.class);
                    if(item != null && item.getType() == ItemType.comment)
                        producer.send(new ProducerRecord<>(topic, item.getBy(), item));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                logger.warn("Failed to retrieve item " + index);
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                if(Thread.currentThread().isInterrupted()) break;
            }
        }
    }

    private static Optional<byte[]> retrieveNews(OkHttpClient httpClient, int i) {
        return retrieveNews(httpClient, i, 3);
    }

    private static Optional<byte[]> retrieveNews(OkHttpClient httpClient, int i, int retries) {
        for (int j = 0; j < retries; j++) {
            try {
                return Optional.of(retrieveNewsInternal(httpClient, i));
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private static byte[] retrieveNewsInternal(OkHttpClient httpClient, int i) throws IOException {
        logger.debug("Accessing Item " + i);
        Request request = new Request.Builder()
                .url("https://hacker-news.firebaseio.com/v0/item/"+ i +".json")
                .build();

        Response response = httpClient.newCall(request).execute();
        return response.body().bytes();
    }

    private static void loadPropertiesFromEnvironment(Properties props, String prefix) {
        Map<String, String> environments = System.getenv();
        for (String env : environments.keySet()) {
            if(env.startsWith(prefix))
                props.put(env.substring(prefix.length()).toLowerCase(Locale.ROOT).replace("_", "."), environments.get(env));
        }
    }

    private static void loadPropertiesFromClasspath(Properties props, String file) {
        try (InputStream kafkaConfiguration = HackerNewsCrawler.class.getResourceAsStream(file)) {
            props.load(kafkaConfiguration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}