package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    List<Item> searchItem(String text);

    List<Item> findByOwner_Id(Long userId);

    List<Item> findByIdAndOwner_Id(Long id, Long userId);

    @Query("select i from Item i where i.request in :requests")
    List<Item> findItemsByRequests(List<ItemRequest> requests);
}
