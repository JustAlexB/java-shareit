package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

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
        when(userService.getUserByID(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        itemService.addComment(commentDto, 1L, 1L);

        verify(commentRepository).save(any(Comment.class));
    }
}
