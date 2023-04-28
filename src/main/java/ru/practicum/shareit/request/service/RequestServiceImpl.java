package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.repository.RequestRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
    }

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequestDto.setRequestor(user);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestMapper.toItemReques(itemRequestDto);
        return requestMapper.toItemRequestDto(requestRepository.save(itemRequest));

    }

    @Override
    public List<ItemRequestDto> getAllUsersRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (from == null && size == null) {
            return requestRepository.findAllByRequestor(user, Sort.by(DESC, "created")).stream()
                    .map(requestMapper::toItemRequestDto)
                    .collect(Collectors.toList());
        }
        if ((from == 0 && size == 0) || (from < 0 || size < 0)) {
            throw new IncorrectParameterException("Запрос без пагинации");
        }
        return requestRepository.findAllByRequestorNot(user, PageRequest.of(from, size, Sort.by("created").ascending()))
                .stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос по ID = " + requestId + " не найден"));
        return requestMapper.toItemRequestDto(request);
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getMyRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> myRequests = requestRepository.findAllByRequestor(user, Sort.by(DESC, "created"));
        return myRequests.stream()
                .map(requestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
