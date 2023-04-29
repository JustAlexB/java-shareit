package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemStorage.InMemoryItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceInMem;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userStorage.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryItemServiceTest {

    protected final Storage<Item> itemStorage = new InMemoryItemStorage();
    protected final Storage<User> userStorage = new InMemoryUserStorage();

    private final ItemServiceInMem itemService = new ItemServiceInMem(itemStorage, userStorage);

    @Test
    void itemValidation() {
        ItemDto itemDto = new ItemDto();
        assertThrows(IncorrectParameterException.class, () -> itemService.validation(itemDto));

    }

    @Test
    void itemValidationPass() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        assertDoesNotThrow(() -> itemService.validation(itemDto));
    }

    @Test
    void createItemFail() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        assertThrows(NotFoundException.class, () -> itemService.create(itemDto, 1L));
    }

    @Test
    void updateItemFail() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        assertThrows(NotFoundException.class, () -> itemService.update(3L, 1L, itemDto));
    }

    @Test
    void getItemById() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        userStorage.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        itemDto.setOwner(user);
        itemService.create(itemDto, user.getId());

        assertNotNull(itemService.getItemByID(1L));
    }

    @Test
    void testSearchItem() {

    }
}
