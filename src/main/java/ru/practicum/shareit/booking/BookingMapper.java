package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.model.BookingDetails;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(booking, BookingDto.class);
    }

    public Booking toBooking(BookingDto bookingDto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(bookingDto, Booking.class);
    }

    public BookingDetails toBookingDetails(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDetails.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();

    }

    public BookingAnswerDto toBookingAnswerDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingAnswerDto.builder()
                .id(booking.getId())
                .item(ItemDto.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build())
                .booker(UserDto.builder()
                        .id(booking.getBooker().getId())
                        .build())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }
}
