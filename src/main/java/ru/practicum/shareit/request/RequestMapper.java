package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class RequestMapper {
    public ItemRequest toItemReques(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestor(),
                itemRequestDto.getCreated(),
                itemRequestDto.getItems()
        );
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems())
                .build();
    }
}
