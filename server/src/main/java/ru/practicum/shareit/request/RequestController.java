package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;

@Controller
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public @ResponseBody ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public @ResponseBody List<ItemRequestDto> getMyRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getMyRequests(userId);
    }

    @GetMapping("/all")
    public @ResponseBody List<ItemRequestDto> getAllRequests(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                             @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
        return requestService.getAllUsersRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public @ResponseBody ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
