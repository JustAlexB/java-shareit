package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND (:state = 'ALL'" +
            "OR :state = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "OR :state = 'PAST' AND b.end < CURRENT_TIMESTAMP " +
            "OR :state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP " +
            "OR :state = 'WAITING' AND b.status = 'WAITING' " +
            "OR :state = 'REJECTED' AND b.status = 'REJECTED') " +
            "ORDER BY b.start DESC")
    List<Booking> getAllByState(@Param("userId") Integer userId, @Param("state") String state);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId " +
            "AND (:state = 'ALL'" +
            "OR :state = 'CURRENT' AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "OR :state = 'PAST' AND b.end < CURRENT_TIMESTAMP " +
            "OR :state = 'FUTURE' AND b.start > CURRENT_TIMESTAMP " +
            "OR :state = 'WAITING' AND b.status = 'WAITING' " +
            "OR :state = 'REJECTED' AND b.status = 'REJECTED') " +
            "ORDER BY b.start DESC")
    List<Booking> getAllForOwner(@Param("userId") Integer userId, @Param("state") String state);

    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
            "where b.item_id = ?1 and b.start_date < ?2 " +
            "order by b.start_date DESC LIMIT 1", nativeQuery = true)
    BookingDetails getLastBooking(Integer itemId, LocalDateTime now);

    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
            "where b.item_id = ?1 and b.start_date > ?2 and not b.status = 'REJECTED'" +
            "order by b.start_date LIMIT 1", nativeQuery = true)
    BookingDetails getNextBooking(Integer itemId, LocalDateTime now);

    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
            "where b.item_id in (?1) and b.start_date < ?2 " +
            "order by b.start_date DESC LIMIT 1", nativeQuery = true)
    List<BookingDetails> getLastBookings(List<Integer> itemId, LocalDateTime now);

    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
            "where b.item_id in (?1) and b.start_date > ?2 and not b.status = 'REJECTED'" +
            "order by b.start_date LIMIT 1", nativeQuery = true)
    List<BookingDetails> getNextBookings(List<Integer> itemId, LocalDateTime now);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(Integer userId, Integer itemId, BookingStatus status, LocalDateTime startDate);
}
