package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoGtw;
import ru.practicum.shareit.item.dto.ItemDtoGtw;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @Valid @Positive @PathVariable Long itemId,
            @Valid @Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Пользователь с userId={} выгружает вещь с itemId={}.", userId, itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDtoGtw item,
                                          @Valid @Positive @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("Запрос пользователя {} на добавление вещи {}", userId, item);
        return itemClient.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateById(@Valid @Positive @PathVariable("itemId") Long itemId,
                                             @Valid @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDtoGtw item) {
        log.info("Запрос на обновление вещи {}, владелец ID = {}", item, userId);
        return itemClient.updateItem(item, userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@Valid @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос всех вещей владельца ID = {}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@Valid @RequestParam(name = "text") String query,
                                             @Valid @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Поиск вещи {} владельца ID = {}", query, userId);
        return itemClient.searchItem(query.toLowerCase(), userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDtoGtw commentDto, @PathVariable Long itemId,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemClient.addComment(commentDto, itemId, userId);
    }


}
