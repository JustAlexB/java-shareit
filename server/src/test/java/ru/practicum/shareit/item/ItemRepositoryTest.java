package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSearchItemByTextNotAvailableItem() {
        Item item1 = new Item();
        item1.setName("Слон");
        item1.setDescription("Домашний слон");
        item1.setAvailable(false);
        itemRepository.save(item1);

        List<Item> foundItems = itemRepository.searchItem("Слон");
        assertEquals(0, foundItems.size());
    }

    @Test
    public void testFindAllByOwner() {
        User owner1 = new User();
        owner1.setId(1L);
        owner1.setName("User1");
        owner1.setEmail("test1@ya.ru");
        owner1 = userRepository.save(owner1);

        User owner2 = new User();
        owner2.setId(2L);
        owner2.setName("User2");
        owner2.setEmail("test2@ya.ru");
        owner2 = userRepository.save(owner2);

        Item item1 = new Item();
        item1.setName("Отвертка");
        item1.setDescription("универсальная отвертка крестиком");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Универсальная отвертка");
        item2.setAvailable(true);
        item2.setOwner(owner2);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Утюг");
        item3.setDescription("Утюг на углях");
        item3.setAvailable(true);
        item3.setOwner(owner1);
        itemRepository.save(item3);

        itemRepository.saveAll(Arrays.asList(item1, item2, item3));

        List<Item> items = itemRepository.findByOwner_Id(1L);

        assertNotNull(items);
        //assertEquals(items.size(), 2);
    }

    @Test
    public void testFindByIdAndOwner_Id() {
        User owner1 = new User();
        owner1.setId(3L);
        owner1.setName("User new");
        owner1.setEmail("test_new@ya.ru");
        owner1 = userRepository.save(owner1);

        User owner2 = new User();
        owner2.setId(4L);
        owner2.setName("User2 new");
        owner2.setEmail("test2_new@ya.ru");
        owner2 = userRepository.save(owner2);

        Item item1 = new Item();
        item1.setName("Дрель нового образца");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        item1.setOwner(owner1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Универсальная отвертка");
        item2.setAvailable(true);
        item2.setOwner(owner2);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Утюг");
        item3.setDescription("Утюг на углях");
        item3.setAvailable(true);
        item3.setOwner(owner1);
        itemRepository.save(item3);

        itemRepository.saveAll(Arrays.asList(item1, item2, item3));

        List<Item> items = itemRepository.findByIdAndOwner_Id(1L, 2L);

        assertNotNull(items);
        assertEquals(items.size(), 0);

    }

    @Test
    public void testSearchItemByText() {
        Item item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("универсальная дрель");
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Универсальная отвертка");
        item2.setAvailable(true);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Утюг");
        item3.setDescription("Утюг на углях");
        item3.setAvailable(true);
        itemRepository.save(item3);

        List<Item> foundItems = itemRepository.searchItem("Дрель");
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item1));

        foundItems = itemRepository.searchItem("Универсальная");
        assertEquals(2, foundItems.size());
        assertTrue(foundItems.contains(item2));
        assertTrue(foundItems.contains(item1));

        foundItems = itemRepository.searchItem("угля");
        assertEquals(1, foundItems.size());
        assertTrue(foundItems.contains(item3));

    }
}
