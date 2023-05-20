package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    public void testDtoSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test name")
                .email("test@ya.ru")
                .build();

        String jsonOutput = json.write(userDto).getJson();
        String expectedJson = "{\"id\":1,\"name\":\"Test name\",\"email\":\"test@ya.ru\"}";

        assertThat(jsonOutput).isEqualTo(expectedJson);
    }

    @Test
    public void testDtoDeserialize() throws Exception {
        UserDto dtoForCompare = UserDto.builder()
                .id(1L)
                .name("Test name")
                .email("test@ya.ru")
                .build();
        String jsonInput = "{\"id\":1,\"name\":\"Test name\",\"email\":\"test@ya.ru\"}";

        UserDto dtoParsed = json.parse(jsonInput).getObject();

        assertThat(dtoForCompare).isEqualTo(dtoParsed);
    }

    @Test
    public void testThrowExceptionWhenEmailIncorrect() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test name")
                .email("testya.ru")
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDto, Create.class);
        assertEquals(1, constraintViolations.size());

        userDto.setEmail(null);
        constraintViolations = validator.validate(userDto, Create.class);
        assertEquals(1, constraintViolations.size());
    }
}
