package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemServiceImpl itemService;

    @Autowired
    public ItemController(UserService userService, ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на добавление новой вещи {}", item);
        return itemService.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateById(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto item) {
        log.info("Запрос на обновление вещи {}, владелец ID = {}", item, userId);
        return itemService.update(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemAnswerDto getItemById(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос вещи {}, владелец ID = {}", itemId, userId);
        return itemService.getItemByID(itemId, userId);
    }

    @GetMapping
    public Collection<ItemAnswerDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос всех вещей владельца ID = {}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<Item> searchItem(@RequestParam(name = "text", required = false) String query,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск вещи {} владельца ID = {}", query, userId);
        return itemService.searchItem(query.toLowerCase());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto, @PathVariable Long itemId,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}
