package org.example.hackernews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.example.hackernews.GenericItem;

@Slf4j
public class ItemSerializer implements Serializer<GenericItem> {

    private ObjectMapper objectMapper;

    public ItemSerializer() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(String topic, GenericItem data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.debug("Unable to serialize object");
            log.debug(e.toString());
            return null;
        }
    }
}
