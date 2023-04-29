package ru.practicum.shareit.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    RequestServiceImpl requestService;
    private final LocalDateTime created = LocalDateTime.parse("2023-04-24T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    @Test
    public void addRequest() throws Exception {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
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

        when(requestService.create(any(), anyLong())).thenReturn(testDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(testDto.getDescription())));
    }

    @Test
    void getRequestById() throws Exception {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
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

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(testDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) 1L)));

        verify(requestService).getRequestById(1L, userId);
    }

    @Test
    public void testGetRequestByIdNotFound() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong())).thenThrow(new ConflictException("not found"));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetMyRequests() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setId(1L);
        requestDto1.setRequestor(user);
        requestDto1.setCreated(created);
        requestDto1.setDescription("Request 1");

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setId(2L);
        requestDto2.setRequestor(user);
        requestDto2.setCreated(created.plusHours(2));
        requestDto2.setDescription("Request 2");

        List<ItemRequestDto> requestList = Arrays.asList(requestDto1, requestDto2);

        when(requestService.getMyRequests(anyLong())).thenReturn(requestList);

        MvcResult mvcResult = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andReturn();

        List<ItemRequestDto> responseList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(responseList.size(), 2);
        assertEquals(requestList.get(0).getDescription(), responseList.get(0).getDescription());
        assertEquals(requestList.get(1).getDescription(), responseList.get(1).getDescription());
        verify(requestService).getMyRequests(1L);
    }

    @Test
    public void testGetAllUsersRequests() throws Exception {
        int from = 0;
        int size = 2;

        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemRequestDto requestDto1 = new ItemRequestDto();
        requestDto1.setId(1L);
        requestDto1.setRequestor(user);
        requestDto1.setCreated(created);
        requestDto1.setDescription("Request 1");

        ItemRequestDto requestDto2 = new ItemRequestDto();
        requestDto2.setId(2L);
        requestDto2.setRequestor(user);
        requestDto2.setCreated(created.plusHours(2));
        requestDto2.setDescription("Request 2");

        List<ItemRequestDto> requestList = Arrays.asList(requestDto1, requestDto2);

        when(requestService.getAllUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(requestList);

        MvcResult mvcResult = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andReturn();
        List<ItemRequestDto> responseList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(responseList.size(), 2);
        assertEquals(requestList.get(0).getDescription(), responseList.get(0).getDescription());
        assertEquals(requestList.get(1).getDescription(), responseList.get(1).getDescription());
        verify(requestService).getAllUsersRequests(1L, from, size);
    }
}
