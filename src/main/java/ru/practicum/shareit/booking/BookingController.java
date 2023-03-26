package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking addBooking(@Valid @RequestBody BookingDto booking,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос на бронирование {} пользователем {}", booking, userId);
        return bookingService.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateStatus(@PathVariable("bookingId") Integer bookingId,
                                @RequestParam(name = "approved") String approved,
                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос на подтверждение/отклонение статуса бронирования {}, владелец ID = {}, статус: {}",
                bookingId, userId, approved);
        return bookingService.updateStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@PathVariable("bookingId") Integer bookingId,
                              @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return bookingService.getBookingByID(bookingId, userId);
    }

    @GetMapping
    public Collection<Booking> getAllWithState(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос всех бронирований по статусу {}, пользователь ID = {}", state, userId);
        return bookingService.getAllByState(userId, state);
    }

    @GetMapping("/owner")
    public Collection<Booking> getAllForOwner(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Запрос всех бронирований по статусу {}, владелец ID = {}", state, userId);
        return bookingService.getAllForOwner(userId, state);
    }

}
