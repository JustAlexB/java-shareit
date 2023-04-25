package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceDB {
    private final Storage<User> userStorage;

    @Autowired
    public UserServiceDB(@Qualifier("dbUserStorage") Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public List<UserDto> getAll() {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public Optional<User> getUserByID(Long userID) {
        return userStorage.getByID(userID);
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUserFromDto(userDto);
        validation(user);
        return UserMapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(UserDto userDto) {
        User user = UserMapper.toUserFromDto(userDto);
        return UserMapper.toUserDto(userStorage.update(user));
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
