package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ItemController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос на добавление новой вещи {}", item);
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateById(@PathVariable("itemId") Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto item) {
        log.info("Запрос на обновление вещи {}, владелец ID = {}", item, userId);
        return itemService.update(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос вещи {}, владелец ID = {}", itemId, userId);
        return itemService.getItemByID(itemId);
    }

    @GetMapping
    public Collection<Item> getAllItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос всех вещей владельца ID = {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<Item> searchItem(@RequestParam(name = "text", required = false) String query, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Поиск вещи {} владельца ID = {}", query, userId);
        return itemService.searchItem(query, userId);
    }
}
