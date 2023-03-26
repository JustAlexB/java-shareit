package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(comment, CommentDto.class);
    }

    public static Comment toComment(CommentDto commentDto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(commentDto, Comment.class);
    }
}
