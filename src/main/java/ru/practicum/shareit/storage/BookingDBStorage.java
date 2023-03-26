package ru.practicum.shareit.storage;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingDataValidationException;
import ru.practicum.shareit.repository.BookingRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Component
public class BookingDBStorage implements Storage<Booking> {
    protected final BookingRepository bookingStorage;

    public BookingDBStorage(BookingRepository bookingStorage) {
        this.bookingStorage = bookingStorage;
    }

    @Override
    public Collection<Booking> getAll() {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        return bookingStorage.findAll(sortById);
    }

    @Override
    @Transactional
    public Booking create(Booking booking) {
        return bookingStorage.save(booking);
    }

    @Override
    @Transactional
    public Booking update(Booking booking) {
        return bookingStorage.save(booking);
    }

    @Override
    public Optional<Booking> getByID(Integer bookingID) {
        return bookingStorage.findById(bookingID);
    }

    @Override
    public Optional<Booking> delByID(Integer bookingID) {
        bookingStorage.deleteById(bookingID);
        return Optional.empty();
    }

    @Override
    public void validation(Booking booking) {
        LocalDateTime startDate = booking.getStart();
        LocalDateTime endDate = booking.getEnd();
        if (startDate == null || endDate == null
                || endDate.isBefore(startDate)
                || endDate.isBefore(LocalDateTime.now())
                || endDate.isEqual(startDate)
                || startDate.isBefore(LocalDateTime.now())) {
            throw new BookingDataValidationException("start: " + startDate + " end: " + endDate);
        }
    }
}
