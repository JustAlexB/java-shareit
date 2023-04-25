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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void testValidationItemDto() {
        ItemDto item1 = new ItemDto();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");

        assertThrows(IncorrectParameterException.class,
                () -> itemService.validationItemDto(item1));

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
    public void testGetAll() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("универсальная отвертка");
        item2.setAvailable(true);

        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);

        when(userRepository.existsById(1L)).thenReturn(false);
        //when(itemRepository.findByOwner_Id(1L)).thenReturn(itemList);

        assertThrows(NotFoundException.class,
                () -> itemService.getAll(1L));

    }
}
