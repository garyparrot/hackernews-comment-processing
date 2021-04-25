package org.example;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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
    private static Properties appConfig;
    private static Properties producerConfig;

    public static void main(String[] args)
    {
        logger.info("Application started");
        appConfig = new Properties();
        producerConfig = new Properties();
        loadPropertiesFromSystemProperties(appConfig);
        loadPropertiesFromClasspath(producerConfig, configurationFileKafka);

        Producer<String, String> producer = new KafkaProducer<>(producerConfig);
        OkHttpClient httpClient = new OkHttpClient();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown producer");
            producer.close();
        }));

        logger.info("Start sending requests");
        start(httpClient, producer);

        producer.flush();
    }

    private static void start(OkHttpClient client, Producer<String, String> producer) {
        final int start = Integer.parseInt(appConfig.getProperty(hackerNewsItemBegin));
        final int offset = Integer.parseInt(appConfig.getProperty(hackerNewsItemOffset));
        final int interval = Integer.parseInt(appConfig.getProperty(hackerNewsCrawInterval));
        final String topic = appConfig.getProperty(kafkaTargetTopic);
        for(int index = start; ; index += offset) {
            Optional<String> body = retrieveNews(client, index);

            if(body.isPresent())
                producer.send(new ProducerRecord<>(topic, Integer.toString(index), body.get()));
            else
                logger.warn("Failed to retrieve item " + index);


            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                if(Thread.currentThread().isInterrupted()) break;
            }
        }
    }

    private static Optional<String> retrieveNews(OkHttpClient httpClient, int i) {
        return retrieveNews(httpClient, i, 3);
    }

    private static Optional<String> retrieveNews(OkHttpClient httpClient, int i, int retries) {
        for (int j = 0; j < retries; j++) {
            try {
                return Optional.of(retrieveNewsInternal(httpClient, i));
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    private static String retrieveNewsInternal(OkHttpClient httpClient, int i) throws IOException {
        logger.debug("Accessing Item " + i);
        Request request = new Request.Builder()
                .url("https://hacker-news.firebaseio.com/v0/item/"+ i +".json")
                .build();

        Response response = httpClient.newCall(request).execute();
        return new String(response.body().bytes());
    }

    private static void loadPropertiesFromSystemProperties(Properties props) {
        loadPropertiesFromClasspath(props, configurationFileApp);

        Properties systemProperties = System.getProperties();
        if(systemProperties.contains(kafkaTargetTopic))
            props.put(kafkaTargetTopic, systemProperties.getProperty(kafkaTargetTopic));
    }

    private static void loadPropertiesFromClasspath(Properties props, String file) {
        try (InputStream kafkaConfiguration = HackerNewsCrawler.class.getResourceAsStream(file)) {
            props.load(kafkaConfiguration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}