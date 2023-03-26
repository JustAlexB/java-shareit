package ru.practicum.shareit.booking.model;
import java.time.LocalDateTime;

public interface BookingDetails {
    //Integer getBookingId();
    Integer getId();
    Integer getBookerId();
    LocalDateTime getBookingDate();

}
