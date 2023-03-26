package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    Integer id;
    String text;
    String authorName;
    LocalDateTime created;
}