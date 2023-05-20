package ru.practicum.shareit.user.userStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.storage.InMemoryStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage extends InMemoryStorage<User> {
    @Override
    public User create(User user) {
        super.create(user);
        user.setId(elementID);
        return user;
    }

    @Override
    public User update(User user) {
        Long currentUserID = user.getId();
        if (elements.containsKey(currentUserID)) {
            User foundUser = elements.get(currentUserID);
            User updatableUser = new User(foundUser.getId(), foundUser.getName(), foundUser.getEmail());
            if (user.getName() != null) {
                updatableUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                updatableUser.setEmail(user.getEmail());
            }
            validation(updatableUser);
            elements.put(updatableUser.getId(), updatableUser);
            return updatableUser;
        } else {
            throw new NotFoundException("Пользователь " + user.toString() + " не найден.");
        }
    }

    public Optional<User> getUserByID(Long userID) {
        return getByID(userID);
    }

    public Optional<User> delUserByID(Long userID) {
        return delByID(userID);
    }

    @Override
    public void validation(User user) {
        if (user.getEmail() == null) {
            throw new IncorrectParameterException("email");
        }
        for (Map.Entry<Long, User> entry : elements.entrySet()) {
            if (user.equals(entry.getValue())) {
                log.info("Пользователь {} не будет добавлен.", user);
                throw new ValidationException("Пользователь с таким email уже существует.");
            }
        }
    }
}
