package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.itemStorage.InMemoryItemStorage;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryItemStorageTest {
    private final InMemoryItemStorage itemStorage = new InMemoryItemStorage();

    @Test
    void testCreateItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test name");

        assertEquals(item, itemStorage.create(item));
    }

    @Test
    void testUpdateItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test name");

        assertEquals(item, itemStorage.update(item));
    }

    @Test
    void testGetById() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test name");
        itemStorage.create(item);

        assertTrue(itemStorage.getByID(1L).isPresent());
    }

    @Test
    void testDelById() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test name");
        itemStorage.create(item);

        assertTrue(itemStorage.delByID(1L).isPresent());
    }
}
