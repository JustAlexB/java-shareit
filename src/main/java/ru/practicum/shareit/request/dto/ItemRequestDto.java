package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    int id;
    String description;
    User requestor;
    LocalDateTime created;
}
