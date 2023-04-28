package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import org.modelmapper.ModelMapper;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

    public User toUserFromDto(UserDto userDto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(userDto, User.class);
    }
}
