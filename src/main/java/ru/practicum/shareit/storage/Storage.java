package ru.practicum.shareit.storage;

import java.util.Collection;
import java.util.Optional;

public interface Storage<T> {
    Collection<T> getAll();

    T create(T element);

    T update(T element);

    Optional<T> getByID(Integer elementID);

    Optional<T> delByID(Integer elementID);

    boolean validation(T element);
}
