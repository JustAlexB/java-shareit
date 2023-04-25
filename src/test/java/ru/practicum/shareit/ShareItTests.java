package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.repository.CommentRepository;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.RequestRepository;
import ru.practicum.shareit.repository.UserRepository;
import org.mockito.Mockito;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
class ShareItTests {
    private UserRepository userRepository;
    private RequestRepository requestRepository;
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        requestRepository = Mockito.mock(RequestRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testCreateItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setName("test Item");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.ru");


        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);

        Optional<User> returnedUser = this.userRepository.findById(1L);
        Assertions.assertTrue(returnedUser.isPresent());

        ItemRequest returnedItem = this.requestRepository.save(itemRequest);
        Assertions.assertNotNull(returnedItem);
    }

    @Test
    void testGetMyRequest() {
        List<ItemRequest> listItemRequests = new ArrayList<>();

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setName("test Item");
        itemRequest.setRequestor(user);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest.setId(2L);
        itemRequest.setName("test Item2");
        itemRequest2.setRequestor(user);

        listItemRequests.add(itemRequest);
        listItemRequests.add(itemRequest2);

        Mockito
                .when(requestRepository.findAllByRequestor(Mockito.any(User.class), Mockito.any(Sort.class)))
                .thenReturn(listItemRequests);

        List<ItemRequest> returnedListItemRequests = this.requestRepository.findAllByRequestor(user, Sort.by(DESC, "created"));
        Assertions.assertEquals(returnedListItemRequests.size(), 2);
    }

    @Test
    void testGetRequestById() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setName("test Item");

        Mockito
                .when(requestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        Optional<ItemRequest> returnedRequest = this.requestRepository.findById(1L);
        Assertions.assertTrue(returnedRequest.isPresent());
    }

    @Test
    void testGetAllUsersRequests() {
        List<ItemRequest> listItemRequests = new ArrayList<>();

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setName("test Item");
        itemRequest.setRequestor(user);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest.setId(2L);
        itemRequest.setName("test Item2");
        itemRequest2.setRequestor(user);

        listItemRequests.add(itemRequest);
        listItemRequests.add(itemRequest2);

        Mockito
                .when(requestRepository.findAllByRequestor(Mockito.any(User.class), Mockito.any(Sort.class)))
                .thenReturn(listItemRequests);
        Mockito
                .when(requestRepository.findAllByRequestorNot(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .thenReturn(listItemRequests);

        List<ItemRequest> returnedListItemRequests = this.requestRepository.findAllByRequestor(user, Sort.by(DESC, "created"));
        Assertions.assertEquals(returnedListItemRequests.size(), 2);

        List<ItemRequest> returnedListItemRequestsNot = this.requestRepository.findAllByRequestorNot(user, PageRequest.of(0, 2, Sort.by("created").ascending()));
        Assertions.assertEquals(returnedListItemRequests.size(), 2);

    }

    @Test
    void testGetAllItems() {
        List<Item> items = new ArrayList<>();

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.ru");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item1");
        item.setOwner(user);

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("Test item2");
        item1.setOwner(user);

        items.add(item1);
        items.add(item);

        Mockito
                .when(itemRepository.findByOwner_Id(Mockito.anyLong()))
                .thenReturn(items);
        List<Item> returnedItems = itemRepository.findByOwner_Id(user.getId());
        Assertions.assertEquals(returnedItems.size(), 2);

    }

}
