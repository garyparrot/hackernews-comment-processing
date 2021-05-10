package org.example.webbackend;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.domain.KeywordMatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Properties;

public class KeywordMatchesConsumerTest {

    @Test
    @Disabled
    public void manuallySendingAKeywordMatchTest() {
        /* Execute this test to manually fire a KeywordMatch avro record into topic keyword-matches */

        final String TOPIC = "keyword-matches";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://localhost:8081");

        Producer<String, KeywordMatch> producer = new KafkaProducer<String, KeywordMatch>(props);

        KeywordMatch record = KeywordMatch.newBuilder()
                .setHackerNewsItemId(0)
                .setCategory("Testing")
                .setKeyword("Test")
                .build();

        producer.send(new ProducerRecord<>("keyword-matches", "TESTING", record));
        producer.flush();
        producer.close();
    }

}
