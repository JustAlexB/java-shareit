package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.ItemMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    private final ItemMapper itemMapper = new ItemMapper();

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor(),
                itemRequestDto.getCreated()
        );
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(itemsDto)
                .build();
    }
}
