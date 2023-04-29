package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.repository.RequestRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    UserRepository userRepository;
    private final LocalDateTime created = LocalDateTime.parse("2023-04-24T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    @Test
    public void testFindAllByRequestorEmpty() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestor(user, Sort.by(DESC, "created"));

        assertTrue(itemRequests.isEmpty());
    }

    @Test
    public void testFindAllByRequestorNotEmpty() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").ascending());
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorNot(user, pageable);

        assertTrue(itemRequests.isEmpty());
    }

    @Test
    public void testFindAllByRequestor() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");
        User savedUser = userRepository.save(user);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setRequestor(savedUser);
        itemRequest1.setDescription("testRequest");
        itemRequest1.setCreated(created.plusDays(2));
        requestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setRequestor(savedUser);
        itemRequest2.setDescription("testRequest next");
        itemRequest2.setCreated(created.plusDays(5));
        requestRepository.save(itemRequest2);

        List<ItemRequest> itemRequests = requestRepository.findAllByRequestor(user, Sort.by(DESC, "created"));;

        assertNotNull(itemRequests);
    }

    @Test
    public void testFindAllByRequestorNot() {
        User user = new User();
        user.setId(5L);
        user.setName("Alex");
        user.setEmail("user1@ya.ru");
        User savedUser = userRepository.save(user);

        User user1 = new User();
        user1.setId(9L);
        user1.setName("Miky");
        user1.setEmail("user2@ya.ru");
        User savedUser1 = userRepository.save(user1);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(6L);
        itemRequest1.setRequestor(savedUser);
        itemRequest1.setDescription("testRequest");
        itemRequest1.setCreated(created);


        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(7L);
        itemRequest2.setRequestor(savedUser1);
        itemRequest2.setDescription("testRequest next");
        itemRequest2.setCreated(created.plusHours(2));


        requestRepository.saveAll(Arrays.asList(itemRequest1, itemRequest2));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").ascending());
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorNot(savedUser, pageable);;

        assertEquals(itemRequests.size(), 1);
    }

}
