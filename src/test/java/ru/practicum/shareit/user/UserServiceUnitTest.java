package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceDB;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @InjectMocks
    private UserServiceDB userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testDeleteUser() {
        Long userId = 1L;

        userService.delUserByID(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testCreateUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Test user");
        existingUser.setEmail("testUser@ya.ru");

        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("Test user")
                .email("testUser@ya.ru")
                .build();

        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(modelMapper.map(userDto, User.class)).thenReturn(existingUser);
        when(modelMapper.map(existingUser, UserDto.class)).thenReturn(userDto);

        userService.create(userDto);

        verify(userRepository, times(1)).save(existingUser);
    }
}
