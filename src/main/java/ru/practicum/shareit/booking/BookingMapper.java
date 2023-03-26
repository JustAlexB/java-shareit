package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import org.modelmapper.ModelMapper;

public class BookingMapper {
    private final static ModelMapper mapper = new ModelMapper();

    public static BookingDto toBookingDto(Booking booking) {
        return mapper.map(booking, BookingDto.class);
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return mapper.map(bookingDto, Booking.class);
    }
}
