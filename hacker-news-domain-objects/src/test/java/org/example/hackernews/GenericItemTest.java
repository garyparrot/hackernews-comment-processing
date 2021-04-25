package org.example.hackernews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericItemTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testTransformStoryItem() throws JsonProcessingException {
        String storyItem = "{\n" +
                "  \"by\" : \"dhouston\",\n" +
                "  \"descendants\" : 71,\n" +
                "  \"id\" : 8863,\n" +
                "  \"kids\" : [ 8952, 9224, 8917, 8884, 8887, 8943, 8869, 8958, 9005, 9671, 8940, 9067, 8908, 9055, 8865, 8881, 8872, 8873, 8955, 10403, 8903, 8928, 9125, 8998, 8901, 8902, 8907, 8894, 8878, 8870, 8980, 8934, 8876 ],\n" +
                "  \"score\" : 111,\n" +
                "  \"time\" : 1175714200,\n" +
                "  \"title\" : \"My YC app: Dropbox - Throw away your USB drive\",\n" +
                "  \"type\" : \"story\",\n" +
                "  \"url\" : \"http://www.getdropbox.com/u/2/screencast.html\"\n" +
                "}";
        GenericItem item = objectMapper.readValue(storyItem, GenericItem.class);
        assertEquals(item.getBy(), "dhouston");
        assertEquals(item.getDescendants(), 71);
        assertEquals(item.getId(), 8863L);
        assertEquals(item.getKids().length, 33);
        assertEquals(item.getTime(), 1175714200L);
        assertEquals(item.getTitle(), "My YC app: Dropbox - Throw away your USB drive");
        assertEquals(item.getUrl(), "http://www.getdropbox.com/u/2/screencast.html");
        assertEquals(item.getType(), ItemType.story);
    }

}
