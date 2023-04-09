package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

import java.awt.print.Pageable;
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

    private List<Booking> getListBookings(Long userId, String state, boolean byOwner) {
        switch (state) {
            case "CURRENT":
                return byOwner ? bookingRepository.getAllForOwnerCurrent(userId) : bookingRepository.getAllByStateCurrent(userId);
            case "PAST":
                return byOwner ? bookingRepository.getAllForOwnerPast(userId) : bookingRepository.getAllByStatePast(userId);
            case "FUTURE":
                return byOwner ? bookingRepository.getAllForOwnerFuture(userId) : bookingRepository.getAllByStateFuture(userId);
            case "WAITING":
                return byOwner ? bookingRepository.getAllForOwnerWaiting(userId) : bookingRepository.getAllByStateWaiting(userId);
            case "REJECTED":
                return byOwner ? bookingRepository.getAllForOwnerRejected(userId) : bookingRepository.getAllByStateRejected(userId);
            default:
                return byOwner ? bookingRepository.getAllForOwnerAll(userId) : bookingRepository.getAllByStateAll(userId);
        }
    }

    private Slice<Booking> getSliceBookings(Long userId, String state, PageRequest pageRequest, boolean byOwner) {
        switch (state) {
            case "CURRENT":
                return byOwner ? bookingRepository.getAllForOwnerCurrent(userId, pageRequest) : bookingRepository.getAllByStateCurrent(userId, pageRequest);
            case "PAST":
                return byOwner ?bookingRepository.getAllForOwnerPast(userId, pageRequest) : bookingRepository.getAllByStatePast(userId, pageRequest);
            case "FUTURE":
                return byOwner ? bookingRepository.getAllForOwnerFuture(userId, pageRequest) : bookingRepository.getAllByStateFuture(userId, pageRequest);
            case "WAITING":
                return byOwner ? bookingRepository.getAllForOwnerWaiting(userId, pageRequest) : bookingRepository.getAllByStateWaiting(userId, pageRequest);
            case "REJECTED":
                return  byOwner ? bookingRepository.getAllForOwnerRejected(userId, pageRequest) : bookingRepository.getAllByStateRejected(userId, pageRequest);
            default:
                return byOwner ? bookingRepository.getAllForOwnerAll(userId, pageRequest) : bookingRepository.getAllByStateAll(userId, pageRequest);
        }
    }

    @Override
    public Collection<BookingAnswerDto> getAllByState(Long userId, String state, Integer from, Integer size) {
        List<Booking> allByState = new ArrayList<>();
        Slice<Booking> allByStateSlice;
        if (Arrays.stream(BookingState.values()).noneMatch(eState -> eState.name().equals(state))) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }
        if (from == null && size == null) {
            allByState = getListBookings(userId, state, false);
        } else {
            if ((from == 0 && size == 0) || (from < 0 || size < 0)) {
                throw new IncorrectParameterException("Запрос без пагинации");
            }
            allByStateSlice = getSliceBookings(userId, state, PageRequest.of(from, size), false);
            while (!allByStateSlice.hasContent() && allByStateSlice.getNumber() > 0) {
                allByStateSlice = getSliceBookings(userId, state, PageRequest.of(allByStateSlice.getNumber() - 1, allByStateSlice.getSize(), allByStateSlice.getSort()), false);
            }
            return allByStateSlice.toList().stream()
                    .map(BookingMapper::toBookingAnswerDto)
                    .collect(Collectors.toList());
        }
        if (allByState.isEmpty()) {
            throw new NotFoundException("Неверные параметры запроса");
        }
        return allByState.stream()
                .map(BookingMapper::toBookingAnswerDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingAnswerDto> getAllForOwner(Long userId, String state, Integer from, Integer size) {
        List<Booking> allForOwner = new LinkedList<>();
        Slice<Booking> allByStateSlice;
        if (Arrays.stream(BookingState.values()).noneMatch(eState -> eState.name().equals(state))) {
            throw new UnsupportedStatusException("UNSUPPORTED_STATUS");
        }
        if (from == null && size == null) {
            allForOwner = getListBookings(userId, state, true);
        } else {
            if ((from == 0 && size == 0) || (from < 0 || size < 0)) {
                throw new IncorrectParameterException("Запрос без пагинации");
            }
            allByStateSlice = getSliceBookings(userId, state, PageRequest.of(from, size), true);
            while (!allByStateSlice.hasContent() && allByStateSlice.getNumber() > 0) {
                allByStateSlice = getSliceBookings(userId, state, PageRequest.of(allByStateSlice.getNumber() - 1, allByStateSlice.getSize(), allByStateSlice.getSort()), true);
            }
            return allByStateSlice.toList().stream()
                    .map(BookingMapper::toBookingAnswerDto)
                    .collect(Collectors.toList());
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
