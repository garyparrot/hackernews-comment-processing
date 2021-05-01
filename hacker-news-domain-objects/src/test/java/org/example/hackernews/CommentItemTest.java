package org.example.hackernews;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentItemTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GenericItem can transform into CommentItem")
    public void testCanTransformFromGenericItem() throws JsonProcessingException {
        String jsonString = "{\n" +
                "  \"by\" : \"norvig\",\n" +
                "  \"id\" : 2921983,\n" +
                "  \"kids\" : [ 2922097, 2922429, 2924562, 2922709, 2922573, 2922140, 2922141 ],\n" +
                "  \"parent\" : 2921506,\n" +
                "  \"text\" : \"Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?\",\n" +
                "  \"time\" : 1314211127,\n" +
                "  \"type\" : \"comment\"\n" +
                "}";

        GenericItem genericItem = objectMapper.readValue(jsonString, GenericItem.class);
        Optional<CommentItem> itemOptional = CommentItem.fromGenericItemAsOptional(genericItem);
        assertTrue(itemOptional.isPresent());

        CommentItem item = itemOptional.get();

        assertEquals(item.getAuthor(), "norvig");
        assertEquals(item.getId(), 2921983L);
        assertEquals(item.getCommentIds().length, 7);
        assertEquals(item.getParent(), 2921506L);
        assertEquals(item.getText(), "Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?");
        assertEquals(item.getTime(), 1314211127L);


    }


}