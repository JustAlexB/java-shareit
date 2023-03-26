package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingDetails;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class ItemDto {
    int id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    User owner;
    Integer requestID;
    BookingDetails lastBooking;
    BookingDetails nextBooking;
    Set<Comment> comments;

    public ItemDto(int id, String name, String description, Boolean available, User owner, Integer requestID, Set<Comment> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestID = requestID;
        this.comments = comments;
    }


}



