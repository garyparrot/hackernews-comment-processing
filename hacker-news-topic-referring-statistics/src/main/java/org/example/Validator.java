package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Validator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"normal-group");

        Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        consumer.subscribe(Collections.singletonList("test-topic-5566"));

        while(true) {
            ConsumerRecords<String, String> poll = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> stringStringConsumerRecord : poll) {

                String content = stringStringConsumerRecord.value();

                try{
                    JsonNode jsonRoot = mapper.readTree(content);
                    if (isValid(jsonRoot)) {
                        System.out.println(content);
                        System.out.println("Ok");
                    } else {
                        System.out.println("Bad");
                    }
                } catch (Exception ignored) {
                    System.out.println("shit");
                }
            }

            consumer.commitSync();
        }
    }

    private static boolean isValid(JsonNode jsonRoot) {
        return jsonRoot.has("sender") && jsonRoot.has("content") && jsonRoot.has("receiver");
    }
}
