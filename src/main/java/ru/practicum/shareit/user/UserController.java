package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.util.Collection;
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
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") Integer userID) {
        log.info("Запрос пользователя по ID: {}", userID);
        Optional<User> fondUser = userService.getUserByID(userID);
        if (fondUser.isEmpty()) {
            log.info("Пользователь c ID {} не найден", userID);
            throw new NotFoundException("Пользователь c ID: " + userID + " не найден");
        }
        return fondUser.get();
    }

    @DeleteMapping("/{userId}")
    public void delUserById(@PathVariable("userId") Integer userID) {
        log.info("Запрос удаления пользователя по ID: {}", userID);
        userService.delUserByID(userID);
    }

    @PostMapping
    public User create(@Validated @RequestBody UserDto userDto) {
        User user = UserMapper.toUserFromDto(userDto);
        userService.create(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@Validated @RequestBody UserDto userDto) {
        User user = UserMapper.toUserFromDto(userDto);
        if (userService.update(user) != null)
            log.info("Обновлен пользователь {}", user);
        else {
            log.info("Пользователь {} не найден", user);
            throw new NotFoundException("Пользователь " + user.toString() + " не найден");
        }
        return user;
    }

    @PatchMapping("/{userId}")
    public User updateById(@PathVariable("userId") Integer userID, @RequestBody UserDto userUpdate) {
        if (userID != null && userID >= 0) {
            userUpdate.setId(userID);
            User userFromDto = toUserFromDto(userUpdate);
            userFromDto = userService.update(userFromDto);
            if (userFromDto != null)
                log.info("Обновлен пользователь {}", userFromDto);
            else {
                log.info("Пользователь {} не найден", userUpdate);
                throw new NotFoundException("Пользователь " + userUpdate + " не найден");
            }
            return userFromDto;
        } else {
            log.info("Указан некорректный ID пользователя {} ", userID);
            throw new NotFoundException("Некорректный ID пользователя");
        }
    }
}
