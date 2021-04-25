package org.example.hackernews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemSerializerTest {

    private ItemSerializer itemSerializer;

    @BeforeEach
    public void setup() {
        itemSerializer = new ItemSerializer();
    }

    @Test
    @DisplayName("Able to serialize GenericItem")
    void serialize() {
        GenericItem item = new GenericItem();
        item.setBy("garyparrot");
        item.setId(5566L);
        item.setText("Hello World");

        byte[] bytes = itemSerializer.serialize("mock", item);
        String asString = new String(bytes);

        assertTrue(asString.matches(".*\"by\":\"garyparrot\".*"));
        assertTrue(asString.matches(".*\"id\":5566.*"));
        assertTrue(asString.matches(".*\"text\":\"Hello World\".*"));
    }
}