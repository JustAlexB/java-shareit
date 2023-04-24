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
    public void getAllBookingsByState() {
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
    public void getAllBookingsByStateWithPageable() {
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
}
