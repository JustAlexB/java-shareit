package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    int id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    User owner;
    Integer requestID;

    public ItemDto(int id, String name, String description, Boolean available, Integer requestID) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestID = requestID;
    }


}



