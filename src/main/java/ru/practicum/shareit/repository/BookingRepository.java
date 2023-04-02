package ru.practicum.shareit.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> getAllByStateAll(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> getAllByStateCurrent(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllByStatePast(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllByStateFuture(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getAllByStateWaiting(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getAllByStateRejected(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    List<Booking> getAllForOwnerAll(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId " +
            "AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> getAllForOwnerCurrent(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllForOwnerPast(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllForOwnerFuture(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getAllForOwnerWaiting(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getAllForOwnerRejected(@Param("userId") Long userId);

//    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date, b.end_date from bookings b " +
//            "where b.item_id = ?1 and b.start_date < ?2 " +
//            "order by b.start_date DESC LIMIT 1", nativeQuery = true)
//    Booking getLastBooking(Long itemId, LocalDateTime now);
//
//    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date, b.end_date from bookings b " +
//            "where b.item_id = ?1 and b.start_date > ?2 and not b.status = 'REJECTED'" +
//            "order by b.start_date LIMIT 1", nativeQuery = true)
//    Booking getNextBooking(Long itemId, LocalDateTime now);
//
//    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
//            "where b.item_id in (?1) and b.start_date < ?2 " +
//            "order by b.start_date DESC LIMIT 1", nativeQuery = true)
//    List<BookingDetails> getLastBookings(List<Long> itemId, LocalDateTime now);
//
//    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
//            "where b.item_id in (?1) and b.start_date > ?2 and not b.status = 'REJECTED'" +
//            "order by b.start_date LIMIT 1", nativeQuery = true)
//    List<BookingDetails> getNextBookings(List<Long> itemId, LocalDateTime now);
//
//    @Query(value = "select b.id as Id, b.booker_id as bookerId, b.start_date as bookingDate from bookings b " +
//            "where b.item_id in (?1) and b.start_date < ?2 or (b.start_date > ?2 and not b.status = 'REJECTED')" +
//            "order by b.start_date", nativeQuery = true)
//    List<BookingDetails> getLastNextBookings(List<Long> itemId, LocalDateTime now);

    List<Booking> findFirstByItem_IdInAndItem_Owner_IdAndStartIsBefore(
            List<Long> itemsId, Long userId, LocalDateTime end, Sort sort);

    List<Booking> findFirstByItem_IdInAndItem_Owner_IdAndStartIsAfterAndStatusIsNot(
            List<Long> itemsId, Long userId, LocalDateTime start, BookingStatus status, Sort sort);

    Booking findFirstByItem_IdAndItem_Owner_IdAndStartIsBefore(
            Long itemId, Long userId, LocalDateTime end, Sort sort);

    Booking findFirstByItem_IdAndItem_Owner_IdAndStartIsAfterAndStatusIsNot(
            Long itemId, Long userId, LocalDateTime start, BookingStatus status, Sort sort);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime startDate);
}
