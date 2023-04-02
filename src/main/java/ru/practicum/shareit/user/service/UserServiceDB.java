package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceDB {
    private final Storage<User> userStorage;

    @Autowired
    public UserServiceDB(@Qualifier("dbUserStorage") Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        return userStorage.getAll();
    }

    public Optional<User> getUserByID(Long userID) {
        return userStorage.getByID(userID);
    }

    public User create(User user) {
        validation(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delUserByID(Long userID) {
        userStorage.delByID(userID);
    }

    public void validation(User user) {
        if (user.getEmail() == null) {
            throw new IncorrectParameterException("email");
        }
    }
}
