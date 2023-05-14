package ru.practicum.shareit.booking.model;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDetails {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}