package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.repository.RequestRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith({SpringExtension.class})
public class RequestServiceUnitTest {
    @InjectMocks
    private RequestServiceImpl requestService;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    UserRepository userRepository;
    private final LocalDateTime created = LocalDateTime.parse("2023-04-24T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    @Test
    public void testCreate() {

        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();

        ItemRequest testRequest = new ItemRequest();
        testRequest.setId(5L);
        testRequest.setName("Маузер");
        testRequest.setDescription("test request");
        testRequest.setCreated(created);
        testRequest.setItems(new ArrayList<>());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestMapper.toItemRequestDto(any())).thenReturn(testDto);
        when(requestMapper.toItemReques(any())).thenReturn(testRequest);
        requestService.create(testDto, user.getId());
        verify(requestRepository, atLeastOnce()).save(any());
    }

    @Test
    public void testGetRequestById() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();

        ItemRequest testRequest = new ItemRequest();
        testRequest.setId(5L);
        testRequest.setName("Маузер");
        testRequest.setDescription("test request");
        testRequest.setCreated(created);
        testRequest.setItems(new ArrayList<>());

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(testRequest));
        when(requestMapper.toItemRequestDto(any())).thenReturn(testDto);

        requestService.getRequestById(user.getId(), testRequest.getId());
        verify(requestRepository, atLeastOnce()).findById(testRequest.getId());
    }

    @Test
    public void testGetMyRequests() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();


        List<ItemRequest> requestList = new ArrayList<>();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestor(any(), any())).thenReturn(requestList);
        when(requestMapper.toItemRequestDto(any())).thenReturn(testDto);

        requestService.getMyRequests(user.getId());
        verify(requestRepository, atLeastOnce()).findAllByRequestor(user, Sort.by(DESC, "created"));
    }

    @Test
    public void testGetAllUsersRequests() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();


        List<ItemRequest> requestList = new ArrayList<>();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestor(any(), any())).thenReturn(requestList);
        when(requestMapper.toItemRequestDto(any())).thenReturn(testDto);

        requestService.getAllUsersRequests(user.getId(), null, null);
        verify(requestRepository, atLeastOnce()).findAllByRequestor(user, Sort.by(DESC, "created"));
    }

    @Test
    public void testGetAllUsersRequestsWithPagination() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();


        List<ItemRequest> requestList = new ArrayList<>();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestor(any(), any())).thenReturn(requestList);
        when(requestMapper.toItemRequestDto(any())).thenReturn(testDto);

        requestService.getAllUsersRequests(user.getId(), 0, 2);
        verify(requestRepository, atLeastOnce()).findAllByRequestorNot(user, PageRequest.of(0, 2, Sort.by("created").ascending()));

    }
}
