package ru.practicum.shareit.gateway;

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
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDtoGtw;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;
    private final String path = "/users";

    @Test
    public void ShouldGetUser() throws Exception {
        UserDtoGtw user = new UserDtoGtw("Alex", "Alex@ya.ru");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(user, HttpStatus.OK);
        when(userClient.getUser(anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alex")));
    }

    @Test
    public void ShouldGetUsers() throws Exception {
        UserDtoGtw user = new UserDtoGtw("Alex", "Alex@ya.ru");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(user, HttpStatus.OK);
        when(userClient.getUsers()).thenReturn(responseEntity);

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alex")));
    }

    @Test
    public void ShouldAddUser() throws Exception {
        UserDtoGtw user = new UserDtoGtw("Alex", "Alex@ya.ru");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(user, HttpStatus.OK);
        when(userClient.addUser(any())).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        UserDtoGtw dtoResponse = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), UserDtoGtw.class);

        assertEquals(user.getName(), dtoResponse.getName());
    }

    @Test
    public void ShouldUpdateUser() throws Exception {
        UserDtoGtw user = new UserDtoGtw("Alex", "Alex@ya.ru");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(user, HttpStatus.OK);
        when(userClient.updateUser(any(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(patch(path + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());

    }

    @Test
    public void ShouldDelUser() throws Exception {
        mockMvc.perform(delete(path + "/1"))
                .andExpect(status().isOk());
        verify(userClient, times(1)).deleteUser(1L);
    }

}
