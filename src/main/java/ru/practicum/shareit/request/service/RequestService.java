package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    List<ItemRequestDto> getAllUsersRequests(Long userId, Integer from, Integer size);

    ItemRequestDto create(ItemRequestDto requestDto, Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getMyRequests(Long userId);

}
