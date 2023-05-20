package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDtoGtw;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@Valid @RequestBody RequestDtoGtw itemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getMyRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return requestClient.getAllUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @Positive @PathVariable Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }

}
