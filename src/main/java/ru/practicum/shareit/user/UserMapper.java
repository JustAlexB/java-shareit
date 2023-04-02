package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import org.modelmapper.ModelMapper;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

    public static User toUserFromDto(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(userDto, User.class);
    }
}
