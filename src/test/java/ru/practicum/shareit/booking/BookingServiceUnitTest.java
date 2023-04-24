package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
public class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper modelMapper;
    @Mock
    private ItemServiceImpl itemService;
    @Mock
    private UserServiceDB userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

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
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByID(1L, 1L));
    }

    @Test
    public void testGetAllForOwner() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name Next");
        user.setEmail("newtest@yandex.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.REJECTED);
        booking1.setStart(now.minusDays(2));
        booking1.setEnd(now.minusHours(2));
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now.minusDays(3));
        booking2.setEnd(now.plusHours(3));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItem(item);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setStart(now.plusDays(6));
        booking3.setEnd(now.plusDays(2));
        booking3.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking3);

        int from = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(from, size, Sort.by("start").ascending());

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllForOwner(1L, "UnKnown", from, size));

//        List<BookingAnswerDto> requestPage = new ArrayList<>();
//        when(bookingService.getAllForOwner(1L, "ALL", from, size)).thenReturn(requestPage);
//
//        List<BookingAnswerDto> result = bookingService.getAllForOwner(1L, "ALL", from, size);
//
//        assertEquals(1, result.size());
        //verify(bookingRepository, times(1)).getAllForOwnerAll(1L, "ALL",pageable);
    }
}
