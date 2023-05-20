package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {
    private final RequestMapper requestMapper = new RequestMapper();

    @Test
    void toItemRequestDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Item Request");

        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest);

        assertAll(
                () -> assertEquals(itemRequestDto.getId(), itemRequest.getId()),
                () -> assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated()),
                () -> assertEquals(itemRequestDto.getRequestor(), itemRequest.getRequestor()),
                () -> assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription())
        );
    }

    @Test
    void toItemRequestDtoWithListItems() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Item Request");

        List<Item> items = new ArrayList<>();

        ItemRequestDto itemRequestDto = requestMapper.toItemRequestDto(itemRequest, items);

        assertAll(
                () -> assertEquals(itemRequestDto.getId(), itemRequest.getId()),
                () -> assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated()),
                () -> assertEquals(itemRequestDto.getRequestor(), itemRequest.getRequestor()),
                () -> assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription())
        );
    }

    @Test
    void toItemRequest() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setRequestor(user);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setDescription("Item Request");

        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);

        assertAll(
                () -> assertEquals(itemRequestDto.getId(), itemRequest.getId()),
                () -> assertEquals(itemRequestDto.getCreated(), itemRequest.getCreated()),
                () -> assertEquals(itemRequestDto.getRequestor(), itemRequest.getRequestor()),
                () -> assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription())
        );
    }
}
