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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDtoGtw;
import ru.practicum.shareit.item.dto.ItemDtoGtw;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private ObjectMapper objectMapper;
    private final String path = "/items";
    private final String xUserId = "X-Sharer-User-Id";

    @Test
    public void ShouldAddItem() throws Exception {
        ItemDtoGtw item = new ItemDtoGtw("Молоток", "обычный красивый красный", true, 1L);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(item, HttpStatus.OK);
        when(itemClient.addItem(any(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(post(path)
                        .header(xUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()));
    }

    @Test
    public void ShouldAddComment() throws Exception {
        CommentDtoGtw comment = new CommentDtoGtw("у меня нет слов");
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(comment, HttpStatus.OK);
        when(itemClient.addComment(any(), anyLong(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(post(path + "/1/comment")
                        .header(xUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(comment.getText()));
    }

    @Test
    public void ShouldUpdateItem() throws Exception {
        ItemDtoGtw item = new ItemDtoGtw("Молоток", "обычный красивый красный", true, 1L);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(item, HttpStatus.OK);
        when(itemClient.updateItem(any(), anyLong(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(patch(path + "/1")
                        .header(xUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()));
    }

    @Test
    public void ShouldSearchItem() throws Exception {
        ItemDtoGtw item = new ItemDtoGtw("Молоток", "обычный красивый красный", true, 1L);
        ItemDtoGtw item1 = new ItemDtoGtw("Дрель", "обычная красивая дрель ", true, 2L);
        List<ItemDtoGtw> items = Arrays.asList(item, item1);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(items, HttpStatus.OK);
        when(itemClient.searchItem(anyString(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path + "/search")
                        .header(xUserId, 1L)
                        .param("text", "ток"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Молоток")));
    }

    @Test
    public void ShouldGetAllItems() throws Exception {
        ItemDtoGtw item = new ItemDtoGtw("Молоток", "обычный красивый красный", true, 1L);
        ItemDtoGtw item1 = new ItemDtoGtw("Дрель", "обычная красивая дрель ", true, 2L);
        List<ItemDtoGtw> items = Arrays.asList(item, item1);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(items, HttpStatus.OK);
        when(itemClient.getAllItems(anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path)
                        .header(xUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Молоток")))
                .andExpect(jsonPath("$[1].name", is("Дрель")));
    }

    @Test
    public void ShouldGetItemById() throws Exception {
        ItemDtoGtw item = new ItemDtoGtw("Молоток", "обычный красивый красный", true, 1L);
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(item, HttpStatus.OK);
        when(itemClient.getItemById(any(),anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path + "/1")
                        .header(xUserId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Молоток")));
    }
}
