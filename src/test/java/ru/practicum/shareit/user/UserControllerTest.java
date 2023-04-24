package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceDB userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test name")
                .email("test1@ya.ru")
                .build();

        when(userService.create(any(UserDto.class))).thenReturn(userDto);
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        UserDto dtoResponse = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertEquals(userDto, dtoResponse);
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("test name")
                .build();

        when(userService.update(any(UserDto.class))).thenReturn(userDto);
        MvcResult mvcResult = mockMvc.perform(patch("/users/" + userDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        UserDto dtoResponse = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertEquals(userDto, dtoResponse);
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delUserByID(1L);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("Test name1")
                .email("test1@ya.ru")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("Test name2")
                .email("test2@ya.ru")
                .build();
        List<UserDto> users = Arrays.asList(userDto1, userDto2);

        when(userService.getAll()).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();

        List<UserDto> dtoListResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals(users, dtoListResponse);
    }

    @Test
    public void testGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test name1");
        user.setEmail("test1@ya.ru");

        when(userService.getUserByID(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test name1")));
    }
}
