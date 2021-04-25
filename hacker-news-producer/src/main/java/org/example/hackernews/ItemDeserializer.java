package org.example.hackernews;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.example.hackernews.CommentItem;
import org.example.hackernews.GenericItem;
import org.example.hackernews.ItemType;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import static com.squareup.okhttp.internal.Internal.logger;

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
