package ru.practicum.shareit.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryStorage<T> implements Storage<T> {
    public HashMap<Long, T> elements = new HashMap<>();
    public Long elementID = 0L;

    @Override
    public Collection<T> getAll() {
        return elements.values();
    }

    @Override
    public T create(T element) {
        elements.put(++elementID, element);
        return element;
    }

    @Override
    public T update(T element) {
        return element;
    }

    @Override
    public Optional<T> getByID(Long elementID) {
        return Optional.ofNullable(elements.get(elementID));
    }

    @Override
    public Optional<T> delByID(Long elementID) {
        return Optional.ofNullable(elements.remove(elementID));
    }

    @Override
    public void validation(T element) {

    }
}
