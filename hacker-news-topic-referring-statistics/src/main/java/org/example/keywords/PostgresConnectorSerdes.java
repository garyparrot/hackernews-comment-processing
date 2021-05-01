package org.example.keywords;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

@Slf4j
public class PostgresConnectorSerdes {

    public static Serde<CdcRecord> create() {
        ObjectMapper objectMapper = new ObjectMapper();
        PostgresCdcSerializer serializer = new PostgresCdcSerializer(objectMapper);
        PostgresCdcDeserializer deserializer = new PostgresCdcDeserializer(objectMapper);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static class PostgresCdcSerializer implements Serializer<CdcRecord> {

        private final ObjectMapper objectMapper;

        public PostgresCdcSerializer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public byte[] serialize(String topic, CdcRecord data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize data:\n" + e.toString());
                return null;
            }
        }
    }
    public static class PostgresCdcDeserializer implements Deserializer<CdcRecord> {

        private final ObjectMapper objectMapper;

        public PostgresCdcDeserializer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public CdcRecord deserialize(String topic, byte[] data) {
            try {
                return objectMapper.readValue(data, CdcRecord.class);
            } catch (IOException e) {
                log.warn("Failed to deserialize data:\n" + e.toString());
                return null;
            }
        }
    }



}
