package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem_Id(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN :itemsId ORDER BY c.id ASC")
    List<Comment> findAllByItemsId(@Param("itemsId") List<Long> itemsId);

    @Query("SELECT c FROM Comment c WHERE c.item in :items and c.author.id = c.item.id ORDER BY c.id ASC")
    List<Comment> findCommentsForItems(List<Item> items);
}
