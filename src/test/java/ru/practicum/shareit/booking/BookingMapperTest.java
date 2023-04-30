package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDetails;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toBookingDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("подушка");
        item.setDescription("чтобы задушиться");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertAll(
                () -> assertEquals(bookingDto.getItemId(), booking.getItem().getId()),
                () -> assertEquals(bookingDto.getStart(), booking.getStart()),
                () -> assertEquals(bookingDto.getEnd(), booking.getEnd())
        );
    }

    @Test
    void toBooking() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("подушка");
        item.setDescription("чтобы задушиться");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now());

        Booking booking = bookingMapper.toBooking(bookingDto);

        assertAll(
                () -> assertEquals(bookingDto.getStart(), booking.getStart()),
                () -> assertEquals(bookingDto.getEnd(), booking.getEnd())
        );
    }

    @Test
    void testToBookingDetailsFail() {
        assertNull(bookingMapper.toBookingDetails(null));
    }

    @Test
    void testToBookingDetailsPass() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("подушка");
        item.setDescription("чтобы задушиться");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());

        BookingDetails bookingDetails = bookingMapper.toBookingDetails(booking);

        assertAll(
                () -> assertEquals(bookingDetails.getStart(), booking.getStart()),
                () -> assertEquals(bookingDetails.getEnd(), booking.getEnd())
        );

    }

    @Test
    void testToBookingAnswerDtoFail() {
        assertNull(bookingMapper.toBookingAnswerDto(null));
    }

    @Test
    void testToBookingAnswerDtoPass() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("подушка");
        item.setDescription("чтобы задушиться");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());

        BookingAnswerDto bookingAnswerDto = bookingMapper.toBookingAnswerDto(booking);

        assertAll(
                () -> assertEquals(bookingAnswerDto.getStart(), booking.getStart()),
                () -> assertEquals(bookingAnswerDto.getEnd(), booking.getEnd())
        );
    }
}
