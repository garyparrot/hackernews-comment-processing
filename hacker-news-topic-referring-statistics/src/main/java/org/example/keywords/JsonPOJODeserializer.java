package org.example.keywords;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class JsonPOJODeserializer<T> implements Deserializer<T> {
    public static final String CONFIG_TARGET_CLASS = "TARGET_CLASS";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> targetClass;

    public JsonPOJODeserializer() {
    }

    public JsonPOJODeserializer(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        targetClass = (Class<T>) configs.get(CONFIG_TARGET_CLASS);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if(data == null)
            return null;

        try {
            return objectMapper.readValue(data, targetClass);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
