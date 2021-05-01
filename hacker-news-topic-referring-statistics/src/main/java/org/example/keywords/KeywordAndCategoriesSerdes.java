package org.example.keywords;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

public final class KeywordAndCategoriesSerdes {

    private KeywordAndCategoriesSerdes() {
        // This class should never initialized.
        throw new AssertionError();
    }

    public static Serde<KeywordAndCategories> create() {
        KacSerializer ser = new KacSerializer();
        KacDeserializer des = new KacDeserializer();
        return Serdes.serdeFrom(ser, des);
    }

    public static class KacSerializer implements Serializer<KeywordAndCategories> {
        private final ObjectMapper objectMapper = new ObjectMapper();
        @Override
        public byte[] serialize(String topic, KeywordAndCategories data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class KacDeserializer implements Deserializer<KeywordAndCategories> {
        private final ObjectMapper objectMapper = new ObjectMapper();
        @Override
        public KeywordAndCategories deserialize(String topic, byte[] data) {
            try {
                KeywordAndCategories kac = objectMapper.readValue(data, KeywordAndCategories.class);
                if(kac == null)
                    throw new NullPointerException();
                return kac;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
