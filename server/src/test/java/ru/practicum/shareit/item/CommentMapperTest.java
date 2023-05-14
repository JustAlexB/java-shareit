package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    @Test
    void testToCommentDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("test@ya.ru");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("test comment");
        comment.setAuthor(user);

        assertEquals(CommentMapper.toCommentDto(comment).getText(), comment.getText());
    }

    @Test
    void testToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(3L);
        commentDto.setText("comment dto test");
        commentDto.setAuthorName("Alex");
        commentDto.setCreated(LocalDateTime.now());

        assertEquals(commentDto.getText(), CommentMapper.toComment(commentDto).getText());
    }
}
