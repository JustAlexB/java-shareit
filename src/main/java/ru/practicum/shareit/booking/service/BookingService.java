package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    Collection<BookingAnswerDto> getAll();

    Collection<BookingAnswerDto> getAllByState(Long userId, String state);

    Collection<BookingAnswerDto> getAllForOwner(Long userId, String state);

    BookingAnswerDto getBookingByID(Long bookingID, Long userID);

    BookingAnswerDto addBooking(BookingDto bookingDto, Long userID);

    BookingAnswerDto updateStatus(Long bookingID, Long userID, String approved);
}
