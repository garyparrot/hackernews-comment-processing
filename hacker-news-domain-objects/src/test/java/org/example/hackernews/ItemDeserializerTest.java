package org.example.hackernews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ItemDeserializerTest {

    private ItemDeserializer deserializer;

    @BeforeEach
    public void setup() {
        deserializer = new ItemDeserializer();
    }

    @Test
    @DisplayName("Able to deserialize object")
    void deserialize() {
        byte[] data = ("{\n" +
                "  \"by\" : \"tel\",\n" +
                "  \"descendants\" : 16,\n" +
                "  \"id\" : 121003,\n" +
                "  \"kids\" : [ 121016, 121109, 121168 ],\n" +
                "  \"score\" : 25,\n" +
                "  \"text\" : \"<i>or</i> HN: the Next Iteration<p>I get the impression that with Arc being released a lot of people who never had time for HN before are suddenly dropping in more often. (PG: what are the numbers on this? I'm envisioning a spike.)<p>Not to say that isn't great, but I'm wary of Diggification. Between links comparing programming to sex and a flurry of gratuitous, ostentatious  adjectives in the headlines it's a bit concerning.<p>80% of the stuff that makes the front page is still pretty awesome, but what's in place to keep the signal/noise ratio high? Does the HN model still work as the community scales? What's in store for (++ HN)?\",\n" +
                "  \"time\" : 1203647620,\n" +
                "  \"title\" : \"Ask HN: The Arc Effect\",\n" +
                "  \"type\" : \"story\"\n" +
                "}").getBytes(StandardCharsets.UTF_8);

        GenericItem item = deserializer.deserialize("mock-topic", data);
        assertEquals(item.getBy(), "tel");
        assertEquals(item.getDescendants(), 16);
        assertEquals(item.getId(), 121003L);
        assertEquals(item.getKids().length, 3);
        assertEquals(item.getScore(), 25L);
        assertEquals(item.getText(), "<i>or</i> HN: the Next Iteration<p>I get the impression that with Arc being released a lot of people who never had time for HN before are suddenly dropping in more often. (PG: what are the numbers on this? I'm envisioning a spike.)<p>Not to say that isn't great, but I'm wary of Diggification. Between links comparing programming to sex and a flurry of gratuitous, ostentatious  adjectives in the headlines it's a bit concerning.<p>80% of the stuff that makes the front page is still pretty awesome, but what's in place to keep the signal/noise ratio high? Does the HN model still work as the community scales? What's in store for (++ HN)?");
        assertEquals(item.getTime(), 1203647620L);
        assertEquals(item.getTitle(), "Ask HN: The Arc Effect");
        assertEquals(item.getType(), ItemType.story);
    }
}