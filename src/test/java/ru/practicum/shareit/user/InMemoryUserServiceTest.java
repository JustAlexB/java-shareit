package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userStorage.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryUserServiceTest {
    private final Storage<User> userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);

    @Test
    void testCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");

        assertEquals(user, userService.create(user));
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");
        userService.create(user);

        User user1 = new User();
        user1.setId(1L);
        user1.setName("Miky");
        user1.setEmail("Miky@ya.ru");

        assertEquals(user1.getName(), userService.update(user1).getName());
    }

    @Test
    void testGetAll() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");
        userService.create(user);

        assertEquals(1, userService.getAll().size());
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");
        userService.create(user);

        assertTrue(userService.getUserByID(1L).isPresent());
    }

    @Test
    void testDelUserById() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");
        userService.create(user);

        assertTrue(userService.delUserByID(1L).isPresent());
    }
}
