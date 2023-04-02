package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    protected final Storage<User> userStorage;

    @Autowired
    public UserService(@Qualifier("inMemoryUserStorage") Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public Optional<User> getUserByID(Long userID) {
        return userStorage.getByID(userID);
    }

    public User create(User user) {
        userStorage.validation(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        User updatableUser = userStorage.update(user);
        return updatableUser;
    }

    public Optional<User> delUserByID(Long userID) {
        return userStorage.delByID(userID);
    }

}
