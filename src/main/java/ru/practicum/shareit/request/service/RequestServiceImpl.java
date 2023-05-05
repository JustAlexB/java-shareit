package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.RequestRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    private final ItemRepository itemRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository, RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequestDto.setRequestor(user);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllUsersRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> notMyRequests = requestRepository.findAllByRequestorNot(user, PageRequest.of(from, size, Sort.by("created").ascending()));
        return addItemsToRequest(notMyRequests);
    }

    @Override
    @Transactional
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос по ID = " + requestId + " не найден"));
        List<ItemRequest> aloneRequest = new ArrayList<>();
        aloneRequest.add(request);

        return addItemsToRequest(aloneRequest).get(0);
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getMyRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> myRequests = requestRepository.findAllByRequestor(user, Sort.by(DESC, "created"));

        return addItemsToRequest(myRequests);
    }

    private List<ItemRequestDto> addItemsToRequest(List<ItemRequest> requests) {
        Map<ItemRequest, List<Item>> items = itemRepository.findItemsByRequests(requests).stream()
                .collect(groupingBy(Item::getRequest, toList()));

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest r : requests) {
            if (!items.containsKey(r)) {
                itemRequestDtoList.add(requestMapper.toItemRequestDto(r));
            } else {
                itemRequestDtoList.add(requestMapper.toItemRequestDto(r, items.get(r)));
            }
        }
        return itemRequestDtoList;
    }
}
