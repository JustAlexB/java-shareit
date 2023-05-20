package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void testUpdateItem() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        ItemDto itemDto = ItemDto.builder()
                .name("Helen")
                .description("new description")
                .build();

        ItemDto updatedItemDto = itemService.update(item1.getId(), user.getId(), itemDto);

        assertEquals(itemDto.getName(), updatedItemDto.getName());
        assertEquals(itemDto.getDescription(), updatedItemDto.getDescription());
    }

    @Test
    public void testUpdateItemIncorrectUserIdNotFoundException() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test6@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        ItemDto itemDto = ItemDto.builder()
                .name("Helen")
                .description("new description")
                .build();

        assertThrows(NotFoundException.class, () -> itemService.update(item1.getId(), 32L, itemDto));
    }

    @Test
    public void testSearchItemByText() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test1@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        List<Item> result = itemService.searchItem("дрель");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualToIgnoringCase("Дрель");
    }

    @Test
    public void testSearchItemByEmptyText() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test2@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        itemRepository.save(item1);

        List<Item> result = itemService.searchItem("");

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void testAddComment() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test3@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        itemRepository.save(item1);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item1);
        bookingRepository.save(booking);

        CommentDto commentDto = CommentDto.builder()
                .text("я не переживу эти тесты")
                .authorName("Alex")
                .build();

        CommentDto createdComment = itemService.addComment(commentDto, item1.getId(), user.getId());

        assertNotNull(createdComment);
        assertEquals(user.getName(), createdComment.getAuthorName());
        assertEquals(commentDto.getText(), createdComment.getText());
        assertEquals(commentDto.getAuthorName(), createdComment.getAuthorName());

    }

    @Test
    public void testAddCommentEmptyText() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test4@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        itemRepository.save(item1);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item1);
        bookingRepository.save(booking);

        CommentDto commentDto = CommentDto.builder()
                .text("")
                .authorName("Alex")
                .build();

        assertThrows(IncorrectParameterException.class, () -> itemService.addComment(commentDto, item1.getId(), user.getId()));
    }

    @Test
    public void testValidationComment() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("test5@ya.ru");
        User savedUser = userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        itemRepository.save(item1);

        LocalDateTime now = LocalDateTime.now();
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item1);
        bookingRepository.save(booking);

        CommentDto commentDto = CommentDto.builder()
                .text("")
                .authorName("Alex")
                .build();


        assertThrows(IncorrectParameterException.class, () -> itemService.validationComment(commentDto, item1.getId(), user.getId()));
    }
}
