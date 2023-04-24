package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    Long id;
    String name;
    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    String email;
}

