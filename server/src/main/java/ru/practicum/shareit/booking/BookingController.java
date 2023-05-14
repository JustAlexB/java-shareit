package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingServiceImpl bookingServiceImpl;

    @Autowired
    public BookingController(BookingServiceImpl bookingServiceImpl) {
        this.bookingServiceImpl = bookingServiceImpl;
    }

    @PostMapping
    public BookingAnswerDto addBooking(@Valid @RequestBody BookingDto booking,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на бронирование {} пользователем {}", booking, userId);
        return bookingServiceImpl.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingAnswerDto updateStatus(@PathVariable("bookingId") Long bookingId,
                                         @RequestParam(name = "approved") String approved,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на подтверждение/отклонение статуса бронирования {}, владелец ID = {}, статус: {}",
                bookingId, userId, approved);
        return bookingServiceImpl.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingAnswerDto getBooking(@PathVariable("bookingId") Long bookingId,
                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingServiceImpl.getBookingByID(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingAnswerDto> getAllWithState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос всех бронирований по статусу {}, пользователь ID = {}", state, userId);
        return bookingServiceImpl.getAllByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingAnswerDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос всех бронирований по статусу {}, владелец ID = {}", state, userId);
        return bookingServiceImpl.getAllForOwner(userId, state, from, size);
    }
}
