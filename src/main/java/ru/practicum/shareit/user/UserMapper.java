package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import org.modelmapper.ModelMapper;

public class UserMapper {
    private final static ModelMapper mapper = new ModelMapper();

    public static UserDto toUserDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    public static User toUserFromDto(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }
}
