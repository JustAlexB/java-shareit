package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceInMem {
    protected final Storage<Item> itemStorage;
    protected final Storage<User> userStorage;
    protected final ItemMapper itemMapper = new ItemMapper();

    @Autowired
    public ItemServiceInMem(@Qualifier("inMemoryItemStorage") Storage<Item> itemStorage, Storage<User> userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Collection<Item> getAll(Long userID) {
        if (userID != null)
            return itemStorage.getAll().stream()
                    .filter(i -> i.getOwner().getId().equals(userID))
                    .collect(Collectors.toCollection(ArrayList::new));

        return itemStorage.getAll();
    }

    public ItemDto getItemByID(Long itemID) {
        ItemDto itemDto = itemMapper.toItemDto(itemStorage.getByID(itemID).get());
        return itemDto;
    }

    public ItemDto create(ItemDto itemDto, Long userID) {
        validation(itemDto);
        Optional<User> user = userStorage.getByID(userID);
        if (user.isEmpty()) {
            throw new NotFoundException("Не найден пользователь по ID = " + userID);
        }
        itemDto.setOwner(user.get());
        Item item = itemMapper.toItem(itemDto, null);
        itemDto = itemMapper.toItemDto(itemStorage.create(item));
        return itemDto;
    }

    public ItemDto update(Long itemID, Long userID, ItemDto itemDto) {
        Optional<User> user = userStorage.getByID(userID);
        Optional<Item> item = itemStorage.getByID(itemID);
        if (user.isEmpty() || item.isEmpty()) {
            throw new NotFoundException("Не удалось найти вещь для обновления");
        }
        if (!item.get().getOwner().getId().equals(userID)) {
            throw new NotFoundException("Нельзя обновлять не свои вещи");
        }
        if (itemDto.getName() != null) {
            item.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.get().setAvailable(itemDto.getAvailable());
        }
        itemDto = itemMapper.toItemDto(itemStorage.update(item.get()));
        return itemDto;
    }

    public Collection<Item> searchItem(String query) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.getAll().stream()
                .filter(Item::isAvailable)
                .filter(i -> i.getName().toLowerCase().contains(query) || i.getDescription().toLowerCase().contains(query))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void validation(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IncorrectParameterException("available");
        }
    }
}
