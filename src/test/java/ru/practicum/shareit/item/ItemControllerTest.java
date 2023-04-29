package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

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
    private ItemServiceImpl itemService;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void testAddItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("test item")
                .description("test description")
                .available(true)
                .build();

        when(itemService.create(eq(itemDto), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void testUpdateById() {
        ItemDto itemDto = ItemDto.builder()
                .name("test item")
                .description("test description")
                .available(true)
                .build();

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/" + 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void testSearchItem() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("my item1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("my item2");

        List<Item> items = Arrays.asList(item1, item2);

        when(itemService.searchItem(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((int) 1)))
                .andExpect(jsonPath("$[0].name", is("my item1")))
                .andExpect(jsonPath("$[1].id", is((int) 2)))
                .andExpect(jsonPath("$[1].name", is("my item2")));
    }

    @SneakyThrows
    @Test
    public void testGetAll() {
        ItemAnswerDto testDto1 = ItemAnswerDto.builder()
                .id(1L)
                .name("Item 1")
                .build();

        ItemAnswerDto testDto2 = ItemAnswerDto.builder()
                .id(2L)
                .name("Item 2")
                .build();

        List<ItemAnswerDto> items = Arrays.asList(testDto1, testDto2);

        when(itemService.getAll(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is((int) 1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].id", is((int) 2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")));
    }

    @SneakyThrows
    @Test
    public void testGetItemById() {
        User user = new User();
        user.setId(1L);
        user.setName("User1");
        user.setEmail("test1@ya.ru");

        ItemAnswerDto testDto1 = ItemAnswerDto.builder()
                .id(1L)
                .name("Item 1")
                .build();

        ItemAnswerDto testDto2 = ItemAnswerDto.builder()
                .id(2L)
                .name("Item 2")
                .build();

        when(itemService.getItemByID(eq(1L), eq(1L))).thenReturn(testDto1);
        mockMvc.perform(get("/items/" + 1L).header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) 1)))
                .andExpect(jsonPath("$.name", is("Item 1")));
    }

    @SneakyThrows
    @Test
    public void testAddComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("Test comment")
                .authorName("Alex")
                .build();

        when(itemService.addComment(eq(commentDto), anyLong(), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andReturn();
    }
}
