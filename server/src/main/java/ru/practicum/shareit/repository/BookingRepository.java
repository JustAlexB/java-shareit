package ru.practicum.shareit.repository;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    List<Booking> getAllByStateAll(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId ORDER BY b.start DESC")
    Slice<Booking> getAllByStateAll(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start ASC")
    List<Booking> getAllByStateCurrent(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId " +
            "AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start ASC")
    Slice<Booking> getAllByStateCurrent(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllByStatePast(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    Slice<Booking> getAllByStatePast(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllByStateFuture(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    Slice<Booking> getAllByStateFuture(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getAllByStateWaiting(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'WAITING' ORDER BY b.start DESC")
    Slice<Booking> getAllByStateWaiting(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getAllByStateRejected(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    Slice<Booking> getAllByStateRejected(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    List<Booking> getAllForOwnerAll(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId ORDER BY b.start DESC")
    Slice<Booking> getAllForOwnerAll(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId " +
            "AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> getAllForOwnerCurrent(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId " +
            "AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start DESC")
    Slice<Booking> getAllForOwnerCurrent(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllForOwnerPast(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    Slice<Booking> getAllForOwnerPast(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> getAllForOwnerFuture(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    Slice<Booking> getAllForOwnerFuture(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getAllForOwnerWaiting(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = 'WAITING' ORDER BY b.start DESC")
    Slice<Booking> getAllForOwnerWaiting(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getAllForOwnerRejected(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :userId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    Slice<Booking> getAllForOwnerRejected(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.item in :items and b.status = 'APPROVED' ORDER BY b.start DESC")
    List<Booking> findApprovedForItems(List<Item> items);

    boolean existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime startDate);
}
