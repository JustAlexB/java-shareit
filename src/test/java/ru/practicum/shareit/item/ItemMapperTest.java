package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("test item description");
        item.setAvailable(true);

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertAll(
                () -> assertEquals(itemDto.getId(), item.getId()),
                () -> assertEquals(itemDto.getDescription(), item.getDescription())
        );
    }

    @Test
    void testToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test item");
        itemDto.setDescription("test item description");
        itemDto.setAvailable(true);

        Item item = itemMapper.toItem(itemDto);

        assertAll(
                () -> assertEquals(itemDto.getId(), item.getId()),
                () -> assertEquals(itemDto.getDescription(), item.getDescription())
        );
    }

    @Test
    void testToItemAnswer() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemAnswerDto itemAnswerDto = new ItemAnswerDto();
        itemAnswerDto.setId(1L);
        itemAnswerDto.setName("Test item");
        itemAnswerDto.setDescription("test item description");
        itemAnswerDto.setAvailable(true);
        itemAnswerDto.setRequestId(1L);
        itemAnswerDto.setComments(null);
        itemAnswerDto.setOwner(user);

        Item item = itemMapper.toItemFromAnswer(itemAnswerDto);

        assertAll(
                () -> assertEquals(itemAnswerDto.getId(), item.getId()),
                () -> assertEquals(itemAnswerDto.getRequestId(), item.getRequestId())
        );
    }

    @Test
    void testToAnswerItemDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("test item description");
        item.setAvailable(true);
        item.setOwner(user);

        ItemAnswerDto itemAnswerDto = itemMapper.toAnswerItemDto(item, null, null, null);

        assertAll(
                () -> assertEquals(itemAnswerDto.getId(), item.getId()),
                () -> assertEquals(itemAnswerDto.getRequestId(), item.getRequestId())
        );
    }
}
