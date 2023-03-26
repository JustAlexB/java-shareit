package ru.practicum.shareit.booking.model;

import lombok.*;
import org.springframework.web.bind.annotation.Mapping;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @OneToOne(fetch = FetchType.EAGER)
    Item item;
    @OneToOne(fetch = FetchType.EAGER)
    User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    BookingStatus status = BookingStatus.WAITING;
    @Column(name = "start_date")
    LocalDateTime start;
    @Column(name = "end_date")
    LocalDateTime end;

    public Booking() {
    }
}
