package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceDB;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.storage.Storage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    protected final Storage<Booking> bookingStorage;
    protected final UserServiceDB userServiceDB;
    protected final ItemServiceDB itemServiceDB;
    private final BookingRepository bookingRepository;


    @Autowired
    public BookingService(@Qualifier("bookingDBStorage") Storage<Booking> bookingStorage, UserServiceDB userServiceDB, ItemServiceDB itemServiceDB, BookingRepository bookingRepository) {
        this.bookingStorage = bookingStorage;
        this.userServiceDB = userServiceDB;
        this.itemServiceDB = itemServiceDB;
        this.bookingRepository = bookingRepository;
    }

    public Collection<BookingDto> getAll() {
        return bookingStorage.getAll().stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public Collection<Booking> getAllByState(Integer userId, String state) {
        if (Arrays.stream(BookingState.values()).noneMatch(eState -> eState.name().equals(state))) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }
        List<Booking> allByState = bookingRepository.getAllByState(userId, state);
        if (allByState.isEmpty()) {
            throw new NotFoundException("Неверные параметры запроса");
        }
        return allByState;
    }

    public Collection<Booking> getAllForOwner(Integer userId, String state) {
        if (Arrays.stream(BookingState.values()).noneMatch(eState -> eState.name().equals(state))) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }
        List<Booking> allForOwner = bookingRepository.getAllForOwner(userId, state);
        if (allForOwner.isEmpty()) {
            throw new NotFoundException("Неверные параметры запроса");
        }
        return allForOwner;
    }

    public Booking getBookingByID(Integer bookingID, Integer userID) {
        Booking booking = bookingStorage.getByID(bookingID)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingID));
        if (booking.getBooker().getId() == userID || booking.getItem().getOwner().getId() == userID) {
            return booking;
        } else {
            throw new NotFoundException("Пользователь не имеет доступа к бронированию: " + userID);
        }
    }

    public Booking addBooking(BookingDto bookingDto, Integer userID) {
        Booking newBooking = BookingMapper.toBooking(bookingDto);
        bookingStorage.validation(newBooking);
        Optional<User> fUser = userServiceDB.getUserByID(userID);
        Item fItem = ItemMapper.toItem(itemServiceDB.getItemByID(bookingDto.getItemId(), userID));
        if (fUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (fItem == null) {
            throw new NotFoundException("Вещь для бронирования не найдена");
        }
        if (fItem.getOwner().getId() == userID) {
            throw new NotFoundException("Вещь не может быть забронирована владельцем");
        }
        if (!fItem.isAvailable()) {
            throw new IncorrectParameterException("Бронирование вещи недоступно");

        }
        newBooking.setItem(fItem);
        newBooking.setBooker(fUser.get());
        newBooking.setId(null);
        return bookingStorage.create(newBooking);
    }

    public Booking updateStatus(Integer bookingID, Integer userID, String approved) {
        Booking fbooking = bookingStorage.getByID(bookingID)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (fbooking.getItem().getOwner().getId() != userID) {
            throw new NotFoundException("Пользователь не может изменять статус бронирования");
        }
        if (!fbooking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IncorrectParameterException("Статус бронирования не может быть изменен");
        }
        if (approved.equals("true")) {
            fbooking.setStatus(BookingStatus.APPROVED);
        } else {
            fbooking.setStatus((BookingStatus.REJECTED));
        }
        return bookingStorage.update(fbooking);
    }

}
