package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class ItemServiceUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;

    @Test
    public void testAddComment() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);

        CommentDto commentDto = CommentDto.builder()
                .text("я не переживу эти тесты")
                .authorName("Alex")
                .build();


        Comment comment = new Comment();
        comment.setText("test comment");
        comment.setAuthor(user);
        comment.setItem(item1);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(eq(1L), eq(1L), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        itemService.addComment(commentDto, 1L, 1L);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    public void testCreateItemSuccess() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("универсальная дрель");
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("универсальная дрель");
        item.setAvailable(true);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);
        when(itemMapper.toItem(any())).thenReturn(item);

        itemService.create(itemDto, user.getId());

        verify(itemRepository, atLeastOnce()).save(item);
    }

    @Test
    public void testUpdateItemSuccess() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("универсальная дрель");
        itemDto.setAvailable(true);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("универсальная дрель");
        item.setOwner(user);
        item.setAvailable(true);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        itemService.update(item.getId(), user.getId(), itemDto);

        verify(itemRepository, atLeastOnce()).save(item);
    }

    @Test
    public void testValidationItemDto() {
        ItemDto item1 = new ItemDto();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");

        assertThrows(IncorrectParameterException.class,
                () -> itemService.validationItemDto(item1));
    }

    @Test
    public void testValidationItemDtoSuccess() {
        ItemDto item1 = new ItemDto();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        assertDoesNotThrow(() -> itemService.validationItemDto(item1));
    }

    @Test
    public void testValidationComment() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);

        CommentDto commentDto = CommentDto.builder()
                .text("")
                .authorName("Alex")
                .build();

        assertThrows(IncorrectParameterException.class,
                () -> itemService.validationComment(commentDto, item1.getId(), user.getId()));
    }

    @Test
    public void testValidationCommentSuccess() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);

        CommentDto commentDto = CommentDto.builder()
                .text("не пустой")
                .authorName("Alex")
                .build();

        when(bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(anyLong(), anyLong(), any(), any())).thenReturn(true);

        assertDoesNotThrow(() -> itemService.validationComment(commentDto, item1.getId(), user.getId()));
    }

    @Test
    public void testGetAll() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        when(userRepository.existsById(1L)).thenReturn(true);

        itemService.getAll(user.getId());

        verify(itemRepository, atLeastOnce()).findByOwner_Id(user.getId());
    }

    @Test
    public void testGetAllFail() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemService.getAll(user.getId()));

    }

    @Test
    public void testGetItemById() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item = new Item();
        item.setId(2L);
        item.setName("Дрель");
        item.setDescription("универсальная дрель");
        item.setAvailable(true);

        ItemAnswerDto itemAnswerDto = new ItemAnswerDto();
        itemAnswerDto.setId(1L);
        itemAnswerDto.setName("Test name");
        itemAnswerDto.setDescription("test desc");
        itemAnswerDto.setAvailable(true);

        List<Item> items = new ArrayList<>();
        List<CommentDto> comments = new ArrayList<>();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemMapper.toAnswerItemDto(any(), any(), any(), any())).thenReturn(itemAnswerDto);

        ItemAnswerDto newAnswerDto = itemService.getItemByID(item.getId(), user.getId());

        assertEquals(newAnswerDto, itemAnswerDto);
    }

    @Test
    public void testGetItemByIdFail() {

        assertThrows(NotFoundException.class,
                () -> itemService.getItemByID(1L, 1L));
    }
}
