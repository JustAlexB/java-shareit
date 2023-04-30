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
    void createItemPass() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        userStorage.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);

        assertNotNull(itemService.create(itemDto, user.getId()));
    }

    @Test
    void updateItemFail() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(true);
        assertThrows(NotFoundException.class, () -> itemService.update(3L, 1L, itemDto));
    }

    @Test
    void updateItemPass() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        userStorage.create(user);

        ItemDto itemDtoNew = new ItemDto();
        itemDtoNew.setName("ПАЯЛЬНИК");
        itemDtoNew.setAvailable(true);
        itemDtoNew.setDescription("все уже не нужен");
        itemDtoNew.setOwner(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Паяльник");
        itemDto.setDescription("нужен срочно");
        itemDto.setAvailable(true);
        itemDto.setOwner(user);
        ItemDto createdItem = itemService.create(itemDto, user.getId());

        assertAll(
                () -> assertNotNull(itemService.update(createdItem.getId(), user.getId(), itemDtoNew)),
                () -> assertEquals(itemDtoNew.getDescription(), itemService.update(createdItem.getId(), user.getId(), itemDtoNew).getDescription())
        );
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
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        userStorage.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Паяльник");
        itemDto.setDescription("нужен срочно");
        itemDto.setAvailable(true);
        itemDto.setOwner(user);
        itemService.create(itemDto, user.getId());

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Кисточка малярная");
        itemDto1.setDescription("кРуГлаЯ");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(user);
        itemService.create(itemDto1, user.getId());

        assertNotNull(itemService.searchItem("круглая"));
    }

    @Test
    void testGetAllWithUserID() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        userStorage.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Паяльник");
        itemDto.setDescription("нужен срочно");
        itemDto.setAvailable(true);
        itemDto.setOwner(user);
        itemService.create(itemDto, user.getId());

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Кисточка малярная");
        itemDto1.setDescription("кРуГлаЯ");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(user);
        itemService.create(itemDto1, user.getId());

        assertEquals(2,itemService.getAll(user.getId()).size());
    }

    @Test
    void testGetAllWithoutUserID() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@ya.ru");
        userStorage.create(user);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Паяльник");
        itemDto.setDescription("нужен срочно");
        itemDto.setAvailable(true);
        itemDto.setOwner(user);
        itemService.create(itemDto, user.getId());

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Кисточка малярная");
        itemDto1.setDescription("кРуГлаЯ");
        itemDto1.setAvailable(true);
        itemDto1.setOwner(user);
        itemService.create(itemDto1, user.getId());

        assertEquals(2,itemService.getAll(null).size());
    }
}
