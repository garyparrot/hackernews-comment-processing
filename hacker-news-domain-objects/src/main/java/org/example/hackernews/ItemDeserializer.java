package org.example.hackernews;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;


@Slf4j
public class ItemDeserializer implements Deserializer<GenericItem> {

    private final ObjectMapper objectMapper;

    public ItemDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public GenericItem deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, GenericItem.class);
        } catch (IOException e) {
            log.debug("Unable to parse target: (" + s + "," + new String(bytes) + ")\n");
            log.debug(e.toString());
        }
        return null;
    }

}
