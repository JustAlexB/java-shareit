package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemAnswerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class BookingServiceUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserServiceDB userServiceDB;
    @Mock
    private ItemServiceImpl itemService;

    @Test
    public void testAddBooking_NullPointer() {
        User user = new User();
        user.setId(5L);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setAvailable(true);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3));

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());

        assertThrows(NullPointerException.class, () -> bookingService.addBooking(bookingDto, 5L));
    }

    @Test
    public void testGetBookingById() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setId(1L);
        user.setName("Test Name Next");
        user.setEmail("newtest@yandex.ru");

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.REJECTED);
        booking1.setStart(now.minusDays(2));
        booking1.setEnd(now.minusHours(2));

        BookingAnswerDto bookingAnswerDto = new BookingAnswerDto();
        bookingAnswerDto.setId(5L);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingMapper.toBookingAnswerDto(any())).thenReturn(bookingAnswerDto);

        bookingService.getBookingByID(booking1.getId(), user.getId());

        verify(bookingRepository, atLeastOnce()).findById(booking1.getId());
    }

    @Test
    public void testAddBooking() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setId(9L);
        owner.setName("Owner Name");
        owner.setEmail("owner@yandex.ru");

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(now.plusDays(2));
        booking.setEnd(now.plusDays(3));

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequestId(null);

        ItemAnswerDto itemAnswerDto = new ItemAnswerDto();
        itemAnswerDto.setId(1L);
        itemAnswerDto.setName("Test name");
        itemAnswerDto.setDescription("test desc");
        itemAnswerDto.setAvailable(true);

        BookingAnswerDto bookingAnswerDto = new BookingAnswerDto();
        bookingAnswerDto.setId(5L);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(10L);

        when(bookingMapper.toBooking(any())).thenReturn(booking);
        when(userServiceDB.getUserByID(anyLong())).thenReturn(Optional.of(user));
        when(itemService.getItemByID(anyLong(), anyLong())).thenReturn(itemAnswerDto);
        when(itemMapper.toItemFromAnswer(any())).thenReturn(item);

        bookingService.addBooking(bookingDto, user.getId());

        verify(bookingRepository, atLeastOnce()).save(any(Booking.class));
    }

    @Test
    public void testUpdateStatus() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner Name");
        owner.setEmail("owner@yandex.ru");

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequestId(null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(now.plusDays(2));
        booking.setEnd(now.plusDays(3));

        BookingAnswerDto bookingAnswerDto = new BookingAnswerDto();
        bookingAnswerDto.setId(5L);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        bookingService.updateStatus(booking.getId(), user.getId(), "true");

        verify(bookingRepository, atLeastOnce()).save(any(Booking.class));
    }

    @Test
    public void testShouldNotUpdateStatus() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner Name");
        owner.setEmail("owner@yandex.ru");

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequestId(null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(now.plusDays(2));
        booking.setEnd(now.plusDays(3));

        BookingAnswerDto bookingAnswerDto = new BookingAnswerDto();
        bookingAnswerDto.setId(5L);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(IncorrectParameterException.class,
                () -> bookingService.updateStatus(booking.getId(), user.getId(), "true"));
    }

    @Test
    public void testGetAllForOwner() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner Name");
        owner.setEmail("owner@yandex.ru");

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequestId(null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(now.plusDays(2));
        booking.setEnd(now.plusDays(3));


        BookingAnswerDto bookingAnswerDto = new BookingAnswerDto();
        bookingAnswerDto.setId(5L);

        List<Booking> requestPage = new ArrayList<>();
        requestPage.add(booking);

        when(bookingRepository.getAllForOwnerRejected(anyLong())).thenReturn(requestPage);
        when(bookingMapper.toBookingAnswerDto(booking)).thenReturn(bookingAnswerDto);

        List<BookingAnswerDto> result = bookingService.getAllForOwner(owner.getId(), booking.getStatus().name(), null, null);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
    }

    @Test
    public void testGetAllForOwnerUnsupportedStatus() {
        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllForOwner(1L, "UnKnown", 0, 1));
    }

    @Test
    public void testGetAllByStateUnsupportedStatus() {
        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllByState(1L, "UnKnown", 0, 1));
    }

    @Test
    public void testGetAllByState() {
        LocalDateTime now = LocalDateTime.now();

        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner Name");
        owner.setEmail("owner@yandex.ru");

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(owner);
        item.setAvailable(true);
        item.setRequestId(null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(now.plusDays(2));
        booking.setEnd(now.plusDays(3));


        BookingAnswerDto bookingAnswerDto = new BookingAnswerDto();
        bookingAnswerDto.setId(5L);

        List<Booking> requestPage = new ArrayList<>();
        requestPage.add(booking);

        when(bookingRepository.getAllByStateRejected(anyLong())).thenReturn(requestPage);
        when(bookingMapper.toBookingAnswerDto(booking)).thenReturn(bookingAnswerDto);

        Collection<BookingAnswerDto> result = bookingService.getAllByState(owner.getId(), booking.getStatus().name(), null, null);

        assertEquals(1, result.size());
    }

    @Test
    public void testGetAll() {
        List<Booking> requestPage = new ArrayList<>();
        when(bookingRepository.findAll()).thenReturn(requestPage);

        bookingService.getAll();

        verify(bookingRepository, atLeastOnce()).findAll();
    }
}
