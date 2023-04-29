package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testGetAllBookingsByState() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name Next");
        user.setEmail("newtest@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        List<Item> itemList = new ArrayList<>();
        itemList.add(savedItem);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.REJECTED);
        booking1.setStart(now.minusDays(2));
        booking1.setEnd(now.minusHours(2));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now.minusDays(3));
        booking2.setEnd(now.plusHours(3));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItem(savedItem);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setStart(now.plusDays(6));
        booking3.setEnd(now.plusDays(2));
        booking3.setStatus(BookingStatus.WAITING);
        booking3.setItem(savedItem);
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.getAllByStateAll(user.getId());
        assertTrue(bookings.contains(booking1));
        assertTrue(bookings.contains(booking2));
        assertTrue(bookings.contains(booking3));

        bookings = bookingRepository.getAllByStateWaiting(user.getId());
        assertTrue(bookings.contains(booking3));

        bookings = bookingRepository.getAllByStateRejected(user.getId());
        assertTrue(bookings.contains(booking1));

        bookings = bookingRepository.findApprovedForItems(itemList);
        assertTrue(bookings.contains(booking2));
    }

    @Test
    public void testGetAllByStateCurrent() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name Next");
        user.setEmail("newtest@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(now.minusDays(2));
        booking1.setEnd(now.plusDays(2));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.getAllByStateCurrent(user.getId());
        assertTrue(bookings.contains(booking1));
    }

    @Test
    public void testGetAllByStatePast() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name Next");
        user.setEmail("newtest@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(now.minusDays(10));
        booking1.setEnd(now.minusDays(2));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.getAllByStatePast(user.getId());
        assertTrue(bookings.contains(booking1));
    }

    @Test
    public void testGetAllByStateFuture() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name Next");
        user.setEmail("newtest@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("ручная дерль СПААААСИТЕ");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.WAITING);
        booking1.setStart(now.plusDays(1));
        booking1.setEnd(now.plusDays(2));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        List<Booking> bookings = bookingRepository.getAllByStateFuture(user.getId());
        assertTrue(bookings.contains(booking1));
    }

    @Test
    public void testGetAllBookingsByStateWithPageable() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(2L);
        item.setName("Отвертка");
        item.setDescription("отвертка простая обычная");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.REJECTED);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.minusHours(1));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now.minusDays(1));
        booking2.setEnd(now.plusHours(1));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItem(savedItem);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setStart(now.plusDays(3));
        booking3.setEnd(now.plusDays(1));
        booking3.setItem(savedItem);
        booking3.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking3);

        int from = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(from, size, Sort.by("start").ascending());

        Slice<Booking> bookings = bookingRepository.getAllByStateAll(user.getId(), pageable);
        assertNotNull(bookings);
        assertTrue(bookings.hasContent());
        assertEquals(size, bookings.getNumberOfElements());
        assertEquals(2, bookings.getContent().size());

        bookings = bookingRepository.getAllByStateWaiting(user.getId(), pageable);
        assertNotNull(bookings);
        assertTrue(bookings.hasContent());
        assertEquals(1, bookings.getNumberOfElements());
        assertEquals(1, bookings.getContent().size());

        bookings = bookingRepository.getAllByStateRejected(user.getId(), pageable);
        assertNotNull(bookings);
        assertTrue(bookings.hasContent());
        assertEquals(1, bookings.getNumberOfElements());
        assertEquals(1, bookings.getContent().size());
    }

    @Test
    public void testGetAllBookingsByStateWithPageableCurrent() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(2L);
        item.setName("Отвертка");
        item.setDescription("отвертка простая обычная");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStart(now.minusDays(1));
        booking1.setEnd(now.plusDays(2));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now.minusDays(1));
        booking2.setEnd(now.plusDays(2));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItem(savedItem);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setStart(now.minusDays(1));
        booking3.setEnd(now.plusDays(2));
        booking3.setItem(savedItem);
        booking3.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking3);

        int from = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(from, size, Sort.by("start").ascending());

        Slice<Booking> bookings = bookingRepository.getAllByStateCurrent(user.getId(), pageable);
        assertNotNull(bookings);
        assertTrue(bookings.hasContent());
        assertEquals(size, bookings.getNumberOfElements());
        assertEquals(2, bookings.getContent().size());
    }

    @Test
    public void testGetAllBookingsByStateWithPageablePast() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(2L);
        item.setName("Отвертка");
        item.setDescription("отвертка простая обычная");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStart(now.minusDays(5));
        booking1.setEnd(now.minusDays(2));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now.minusDays(5));
        booking2.setEnd(now.minusDays(1));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItem(savedItem);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setStart(now.minusDays(10));
        booking3.setEnd(now.minusDays(8));
        booking3.setItem(savedItem);
        booking3.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking3);

        int from = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(from, size, Sort.by("start").ascending());

        Slice<Booking> bookings = bookingRepository.getAllByStatePast(user.getId(), pageable);
        assertNotNull(bookings);
        assertTrue(bookings.hasContent());
        assertEquals(size, bookings.getNumberOfElements());
        assertEquals(2, bookings.getContent().size());
    }

    @Test
    public void testGetAllBookingsByStateWithPageableFuture() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Test Name");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        Item item = new Item();
        item.setId(2L);
        item.setName("Отвертка");
        item.setDescription("отвертка простая обычная");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        Item savedItem = itemRepository.save(item);

        Booking booking1 = new Booking();
        booking1.setBooker(user);
        booking1.setStatus(BookingStatus.APPROVED);
        booking1.setStart(now.plusDays(5));
        booking1.setEnd(now.plusDays(9));
        booking1.setItem(savedItem);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now.plusDays(5));
        booking2.setEnd(now.plusDays(9));
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setItem(savedItem);
        bookingRepository.save(booking2);

        Booking booking3 = new Booking();
        booking3.setBooker(user);
        booking3.setStart(now.plusDays(5));
        booking3.setEnd(now.plusDays(9));
        booking3.setItem(savedItem);
        booking3.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking3);

        int from = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(from, size, Sort.by("start").ascending());

        Slice<Booking> bookings = bookingRepository.getAllByStateFuture(user.getId(), pageable);
        assertNotNull(bookings);
        assertTrue(bookings.hasContent());
        assertEquals(size, bookings.getNumberOfElements());
        assertEquals(2, bookings.getContent().size());
    }

    @Test
    public void testExistsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore_shouldReturnFalse() {
        long userId = 1L;
        long itemId = 3L;
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        BookingStatus status = BookingStatus.APPROVED;

        User user = new User();
        user.setName("Alexey");
        user.setEmail("Alexey@yandex.ru");
        User savedUser = userRepository.save(user);

        User owner = new User();
        owner.setName("Miky");
        owner.setEmail("Miky@ya.ru");
        owner = userRepository.save(owner);

        Item item = new Item();
        item.setName("Дрель дизельная");
        item.setDescription("ручная дерль дизельная");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequestId(null);
        itemRepository.save(item);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(status);
        booking.setStart(startDate);
        bookingRepository.save(booking);

        boolean exists = bookingRepository.existsBookingByBooker_IdAndItem_IdAndStatusAndStartBefore(userId, itemId, status, LocalDateTime.now());

        assertFalse(exists);
    }
}
