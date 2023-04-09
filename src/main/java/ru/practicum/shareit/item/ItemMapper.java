package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.BookingDetails;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemAnswerDto toAnswerItemDto(Item item, BookingDetails lastBooking, BookingDetails nextBooking,
                                                List<CommentDto> comments) {
        return ItemAnswerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .requestId(item.getRequestId() == null ? null : item.getRequestId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static Item toItemFromAnswer(ItemAnswerDto itemAnswerDto) {
        return new Item(
                itemAnswerDto.getId(),
                itemAnswerDto.getName(),
                itemAnswerDto.getDescription(),
                itemAnswerDto.getAvailable(),
                UserMapper.toUserFromDto(itemAnswerDto.getOwner()),
                itemAnswerDto.getRequestId()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequestId()
        );
    }
}
