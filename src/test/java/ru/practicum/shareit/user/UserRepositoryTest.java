package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserFindByEmail() {
        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        userRepository.save(user);
        List<User> createdUser = userRepository.findUserByEmail(user.getEmail());

        assertFalse(createdUser.isEmpty());
        assertEquals(createdUser.get(0).getEmail(), user.getEmail());
    }

    @Test
    public void testUserUpdateEmail() {
        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        userRepository.save(user);
        userRepository.updateEmail("updateEmail@ya.ru", user.getId());

        List<User> createdUser = userRepository.findUserByEmail("updateEmail@ya.ru");

        assertFalse(createdUser.isEmpty());
    }

    @Test
    public void testUserUpdateName() {
        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        userRepository.save(user);
        List<User> createdUser = userRepository.findUserByEmail(user.getEmail());

        assertFalse(createdUser.isEmpty());
        assertEquals(createdUser.get(0).getName(), user.getName());

        user.setName("New test name");
        userRepository.save(user);

        createdUser = userRepository.findUserByEmail(user.getEmail());

        assertFalse(createdUser.isEmpty());
        assertEquals(createdUser.get(0).getName(), user.getName());
    }


}
