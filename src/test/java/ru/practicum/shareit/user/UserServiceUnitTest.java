package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserServiceDB userService;
    @Mock
    private Storage<User> userStorage;
    @Mock
    private UserMapper userMapper;

    @Test
    public void testDeleteUser() {
        Long userId = 1L;
        userService.delUserByID(userId);
        verify(userStorage, times(1)).delByID(userId);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("testUser@ya.ru");

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test user")
                .email("testUser@ya.ru")
                .build();

        when(userMapper.toUserFromDto(any())).thenReturn(user);
        when(userStorage.create(any(User.class))).thenReturn(user);

       userService.create(userDto);
       verify(userStorage, atLeastOnce()).create(user);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("testUser@ya.ru");

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Update user")
                .email("UpdateUser@ya.ru")
                .build();

        when(userMapper.toUserFromDto(any())).thenReturn(user);
        when(userStorage.update(any(User.class))).thenReturn(user);

        userService.update(userDto);
        verify(userStorage, atLeastOnce()).update(user);
    }

    @Test
    public void testValidation() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Test user");

        assertThrows(IncorrectParameterException.class,
                () -> userService.validation(existingUser));

    }

    @Test
    void testGetAll() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setId(((long) i));
            user.setName("User" + i);
            user.setEmail("TestUser" + i + "@ya.ru");
            users.add(user);
        }

        when(userStorage.getAll()).thenReturn(users);

        List<UserDto> listOfUsers = userService.getAll();

        verify(userStorage, times(1)).getAll();
        assertEquals(listOfUsers.size(), 3);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("testUser@ya.ru");

        when(userStorage.getByID(anyLong())).thenReturn(Optional.of(user));

        userService.getUserByID(1L);

        verify(userStorage, times(1)).getByID(1L);
    }

    @Test
    void testDelUserById() {
        User user = new User();
        user.setId(1L);
        user.setName("Test user");
        user.setEmail("testUser@ya.ru");

        when(userStorage.delByID(anyLong())).thenReturn(Optional.of(user));

        userService.delUserByID(1L);

        verify(userStorage, times(1)).delByID(1L);
    }
}
