package ru.practicum.shareit.storage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryStorage<T> implements Storage<T> {
    public HashMap<Integer, T> elements = new HashMap<>();
    public Integer elementID = 0;

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
    public Optional<T> getByID(Integer elementID) {
        return Optional.ofNullable(elements.get(elementID));
    }

    @Override
    public Optional<T> delByID(Integer elementID) {
        return Optional.ofNullable(elements.remove(elementID));
    }

    @Override
    public boolean validation(T element) {
        return false;
    }
}
