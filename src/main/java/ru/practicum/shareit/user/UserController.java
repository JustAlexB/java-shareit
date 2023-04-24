package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.service.UserServiceDB;


import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserServiceDB userService;

    @Autowired
    public UserController(UserServiceDB userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") Long userID) {
        log.info("Запрос пользователя по ID: {}", userID);
        Optional<User> fondUser = userService.getUserByID(userID);
        if (fondUser.isEmpty()) {
            log.info("Пользователь c ID {} не найден", userID);
            throw new NotFoundException("Пользователь c ID: " + userID + " не найден");
        }
        return fondUser.get();
    }

    @DeleteMapping("/{userId}")
    public void delUserById(@PathVariable("userId") Long userID) {
        log.info("Запрос удаления пользователя по ID: {}", userID);
        userService.delUserByID(userID);
    }

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        UserDto createdUser = userService.create(userDto);
        log.info("Добавлен пользователь {}", userDto);
        return createdUser;
    }

    @PutMapping
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto) {
        if (userService.update(userDto) != null)
            log.info("Обновлен пользователь {}", userDto);
        else {
            log.info("Пользователь {} не найден", userDto);
            throw new NotFoundException("Пользователь " + userDto.toString() + " не найден");
        }
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateById(@PathVariable("userId") Long userID, @RequestBody UserDto userUpdate) {
        if (userID != null && userID >= 0) {
            userUpdate.setId(userID);
            UserDto userUpdated = userService.update(userUpdate);
            if (userUpdated != null)
                log.info("Обновлен пользователь {}", userUpdated);
            else {
                log.info("Пользователь {} не найден", userUpdate);
                throw new NotFoundException("Пользователь " + userUpdate + " не найден");
            }
            return userUpdated;
        } else {
            log.info("Указан некорректный ID пользователя {} ", userID);
            throw new NotFoundException("Некорректный ID пользователя");
        }
    }
}
