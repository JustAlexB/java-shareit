package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable("userId") Integer userID){
        log.info("Запрос пользователя по ID: {}", userID);
        User fondUser = userService.getUserByID(userID).get();
        if (fondUser == null) {
            log.info("Пользователь c ID {} не найден", userID);
            throw new NotFoundException("Пользователь c ID: " + userID + " не найден");
        }
        return fondUser;
    }

    @DeleteMapping("/{userId}")
    public User delUserById(@PathVariable("userId") Integer userID){
        log.info("Запрос пользователя по ID: {}", userID);
        Optional<User> fondUser = userService.delUserByID(userID);
        if (fondUser.isEmpty()) {
            log.info("Пользователь c ID {} не удален", userID);
            throw new NotFoundException("Пользователь c ID: " + userID + " не удален");
        }
        return fondUser.get();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        userService.create(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
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
        if (userID != null && userID >=0) {
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
