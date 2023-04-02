package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingDataValidationException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    protected final UserServiceDB userServiceDB;
    protected final ItemServiceImpl itemServiceImpl;
    private final BookingRepository bookingRepository;


    @Autowired
    public BookingServiceImpl(UserServiceDB userServiceDB, ItemServiceImpl itemServiceImpl, BookingRepository bookingRepository) {
        this.userServiceDB = userServiceDB;
        this.itemServiceImpl = itemServiceImpl;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Collection<BookingAnswerDto> getAll() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper::toBookingAnswerDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingAnswerDto> getAllByState(Long userId, String state) {
        List<Booking> allByState = new LinkedList<>();
        if (Arrays.stream(BookingState.values()).noneMatch(eState -> eState.name().equals(state))) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }
        switch (state) {
            case "CURRENT":
                allByState = bookingRepository.getAllByStateCurrent(userId);
                break;
            case "PAST":
                allByState = bookingRepository.getAllByStatePast(userId);
                break;
            case "FUTURE":
                allByState = bookingRepository.getAllByStateFuture(userId);
                break;
            case "WAITING":
                allByState = bookingRepository.getAllByStateWaiting(userId);
                break;
            case "REJECTED":
                allByState = bookingRepository.getAllByStateRejected(userId);
                break;
            default:
                allByState = bookingRepository.getAllByStateAll(userId);
        }
        if (allByState.isEmpty()) {
            throw new NotFoundException("Неверные параметры запроса");
        }
        return allByState.stream()
                .map(BookingMapper::toBookingAnswerDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingAnswerDto> getAllForOwner(Long userId, String state) {
        List<Booking> allForOwner = new LinkedList<>();
        if (Arrays.stream(BookingState.values()).noneMatch(eState -> eState.name().equals(state))) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }
        switch (state) {
            case "CURRENT":
                allForOwner = bookingRepository.getAllForOwnerCurrent(userId);
                break;
            case "PAST":
                allForOwner = bookingRepository.getAllForOwnerPast(userId);
                break;
            case "FUTURE":
                allForOwner = bookingRepository.getAllForOwnerFuture(userId);
                break;
            case "WAITING":
                allForOwner = bookingRepository.getAllForOwnerWaiting(userId);
                break;
            case "REJECTED":
                allForOwner = bookingRepository.getAllForOwnerRejected(userId);
                break;
            default:
                allForOwner = bookingRepository.getAllForOwnerAll(userId);
        }
        if (allForOwner.isEmpty()) {
            throw new NotFoundException("Неверные параметры запроса");
        }
        return allForOwner.stream()
                .map(BookingMapper::toBookingAnswerDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingAnswerDto getBookingByID(Long bookingID, Long userID) {
        Booking booking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingID));
        if (booking.getBooker().getId().equals(userID) || booking.getItem().getOwner().getId().equals(userID)) {
            return BookingMapper.toBookingAnswerDto(booking);
        } else {
            throw new NotFoundException("Пользователь не имеет доступа к бронированию: " + userID);
        }
    }

    @Transactional
    @Override
    public BookingAnswerDto addBooking(BookingDto bookingDto, Long userID) {
        Booking newBooking = BookingMapper.toBooking(bookingDto);
        validation(newBooking);
        Optional<User> fUser = userServiceDB.getUserByID(userID);
        Item fItem = ItemMapper.toItemFromAnswer(itemServiceImpl.getItemByID(bookingDto.getItemId(), userID));
        if (fUser.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (fItem.getOwner().getId().equals(userID)) {
            throw new NotFoundException("Вещь не может быть забронирована владельцем");
        }
        if (!fItem.isAvailable()) {
            throw new IncorrectParameterException("Бронирование вещи недоступно");

        }
        newBooking.setItem(fItem);
        newBooking.setBooker(fUser.get());
        newBooking.setId(null);
        return BookingMapper.toBookingAnswerDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingAnswerDto updateStatus(Long bookingID, Long userID, String approved) {
        Booking fbooking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!fbooking.getItem().getOwner().getId().equals(userID)) {
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
        return BookingMapper.toBookingAnswerDto(bookingRepository.save(fbooking));
    }

    private void validation(Booking booking) {
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
