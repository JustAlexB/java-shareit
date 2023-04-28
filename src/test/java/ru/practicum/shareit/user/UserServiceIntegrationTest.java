package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserServiceDB userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("Alex")
                .email("test@ya.ru")
                .build();

        UserDto createdUserDto = userService.create(userDto);

        assertEquals(userDto.getName(), createdUserDto.getName());
        assertEquals(userDto.getEmail(), createdUserDto.getEmail());

        User createdUser = userRepository.findById(createdUserDto.getId()).orElse(null);

        assertNotNull(createdUser);
        assertEquals(userDto.getName(), createdUser.getName());
        assertEquals(userDto.getEmail(), createdUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test userDto")
                .email("testDto@ya.ru")
                .build();

        UserDto updatedUser = userService.update(userDto);

        assertNotNull(updatedUser);
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("test user");
        user.setEmail("test@ya.ru");
        User createdUser = userRepository.save(user);

        assertDoesNotThrow(() -> userService.delUserByID(createdUser.getId()));
    }

    @Test
    public void testGetAllUsers() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@ya.ru");
        userRepository.save(user);

        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@ya.ru");
        userRepository.save(user1);

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals("User", result.get(0).getName());
        assertEquals("user@ya.ru", result.get(0).getEmail());
        assertEquals("User1", result.get(1).getName());
        assertEquals("user1@ya.ru", result.get(1).getEmail());
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setName("test user");
        user.setEmail("test@ya.ru");
        User createdUser = userRepository.save(user);

        Optional<User> existUser = userService.getUserByID(createdUser.getId());
        assertTrue(existUser.isPresent());
        assertEquals(existUser.get().getEmail(), user.getEmail());
    }

    @Test
    public void testValidationUser() {
        User user = new User();
        user.setName("test user");
        assertThrows(IncorrectParameterException.class, () -> userService.validation(user));
    }
}
