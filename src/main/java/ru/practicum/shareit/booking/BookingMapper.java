package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import org.modelmapper.ModelMapper;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(booking, BookingDto.class);
    }

    public static Booking toBooking(BookingDto bookingDto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(bookingDto, Booking.class);
    }
}
