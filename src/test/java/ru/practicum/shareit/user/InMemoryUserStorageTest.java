package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userStorage.InMemoryUserStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryUserStorageTest {
    private final static InMemoryUserStorage userStorage = new InMemoryUserStorage();

    @Test
    void testValidationIncorrectParameter() {
        User user = new User();
        user.setId(1L);
        assertThrows(IncorrectParameterException.class, () -> userStorage.validation(user));
    }

    @Test
    void testValidationException() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");

        userStorage.elements.put(1L, user);
        assertThrows(ValidationException.class, () -> userStorage.validation(user));
    }

    @Test
    void testValidationPass() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");

        User user1 = new User();
        user1.setId(2L);
        user1.setEmail("Miky@ya.ru");

        userStorage.elements.put(1L, user);

        assertDoesNotThrow(() -> userStorage.validation(user1));
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setId(1L);
        assertEquals(user, userStorage.create(user));
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");

        User user1 = new User();
        user1.setId(1L);
        user1.setName("Miky");
        user1.setEmail("Miky@ya.ru");

        userStorage.elements.put(1L, user);

        assertEquals(user1.getName(), userStorage.update(user1).getName());
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");

        userStorage.elements.put(1L, user);

        assertEquals(Optional.of(user), userStorage.getUserByID(1L));
    }

    @Test
    void testDelUserById() {
        User user = new User();
        user.setId(1L);
        user.setEmail("Alex@ya.ru");

        userStorage.elements.put(1L, user);

        assertEquals(Optional.of(user), userStorage.delUserByID(1L));
    }

}
