package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    private final static ModelMapper mapper = new ModelMapper();

    public static CommentDto toCommentDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
    }

    public static Comment toComment(CommentDto commentDto) {
        return mapper.map(commentDto, Comment.class);
    }
}
