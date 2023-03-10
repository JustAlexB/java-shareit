package ru.practicum.shareit.item.itemStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storage.InMemoryStorage;

import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryItemStorage extends InMemoryStorage<Item> {
    @Override
    public Collection<Item> getAll() {
        return elements.values();
    }

    @Override
    public Item create(Item item) {
        super.create(item);
        item.setId(elementID);
        return item;
    }

    @Override
    public Item update(Item item) {
        elements.put(item.getId(), item);
        return item;
    }

    public Optional<Item> getByID(Integer itemID) {
        return super.getByID(itemID);
    }

    @Override
    public Optional<Item> delByID(Integer itemID) {
        return super.delByID(itemID);
    }

}
