package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    Long id;
    String name;
    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    String email;
}

