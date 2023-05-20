package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<ItemAnswerDto> getAll(Long userId);

    ItemAnswerDto getItemByID(Long itemID, Long userId);

    ItemDto create(ItemDto itemDto, Long userID);

    ItemDto update(Long itemID, Long userID, ItemDto itemDto);

    List<Item> searchItem(String query);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    void validationItemDto(ItemDto itemDto);

    void validationComment(CommentDto commentDto, Long itemId, Long userId);
}
