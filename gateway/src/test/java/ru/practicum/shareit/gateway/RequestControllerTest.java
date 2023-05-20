package ru.practicum.shareit.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.RequestDtoGtw;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RequestClient requestClient;
    @Autowired
    private ObjectMapper objectMapper;
    private final String path = "/requests";
    private final String xUserId = "X-Sharer-User-Id";

    @Test
    public void ShouldAddRequest() throws Exception {
        RequestDtoGtw request = new RequestDtoGtw("мне нужна веревка и мыльце");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(request, HttpStatus.OK);
        when(requestClient.create(any(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(post(path)
                        .header(xUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    public void ShouldGetMyRequests() throws Exception {
        RequestDtoGtw request = new RequestDtoGtw("мне нужна веревка и мыльце");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(request, HttpStatus.OK);
        when(requestClient.getMyRequests(anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path)
                        .header(xUserId, 1L))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getMyRequests(1L);
    }

    @Test
    public void ShouldGetRequestById() throws Exception {
        RequestDtoGtw request = new RequestDtoGtw("мне нужна веревка и мыльце");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(request, HttpStatus.OK);
        when(requestClient.getRequestById(anyLong(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path + "/{requestId}", 1L)
                        .header(xUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(request.getDescription()));

        verify(requestClient).getRequestById(1L, 1L);
    }

    @Test
    public void ShouldGetAllRequests() throws Exception {
        int from = 0;
        int size = 2;
        RequestDtoGtw request = new RequestDtoGtw("мне нужна веревка и мыльце");
        RequestDtoGtw request1 = new RequestDtoGtw("плотный черный пакет");
        List<RequestDtoGtw> requestList = Arrays.asList(request, request1);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(requestList, HttpStatus.OK);
        when(requestClient.getRequestById(anyLong(), anyLong())).thenReturn(responseEntity);


        when(requestClient.getAllUsersRequests(anyLong(), anyInt(), anyInt())).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(get(path + "/all")
                        .header(xUserId, 1L)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andReturn();
        List<RequestDtoGtw> responseList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(responseList.size(), 2);
        verify(requestClient).getAllUsersRequests(1L, from, size);
    }
}
