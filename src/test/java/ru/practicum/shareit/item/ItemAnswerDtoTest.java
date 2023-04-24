package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemAnswerDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class ItemAnswerDtoTest {
    @Autowired
    private JacksonTester<ItemAnswerDto> json;

    @Test
    public void testSerializeDto() throws Exception {
        ItemAnswerDto testDto = ItemAnswerDto.builder()
                .id(1L)
                .name("Item1")
                .build();

        String jsonOutput = json.write(testDto).getJson();
        String expectedJson = "{\"id\":1,\"name\":\"Item1\",\"description\":null,\"available\":null,\"owner\":null,\"requestId\":null,\"lastBooking\":null," +
                "\"nextBooking\":null,\"comments\":null}";

        assertThat(jsonOutput).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserializeDto() throws Exception {
        String jsonInput = "{\"id\":1,\"name\":\"Item test Dto\",\"email\":\"test@ya.ru\"}";
        ItemAnswerDto testDto = ItemAnswerDto.builder()
                .id(1L)
                .name("Item test Dto")
                .build();

        ItemAnswerDto dto = json.parse(jsonInput).getObject();

        assertThat(dto).isEqualTo(testDto);
    }

    @Test
    public void testIncorrectDto() {
        ItemAnswerDto testDto = ItemAnswerDto.builder()
                .id(1L)
                .name(null)
                .description(null)
                .available(null)
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ItemAnswerDto>> violations = validator.validate(testDto);

        assertEquals(3, violations.size());
        assertTrue(violations.stream()
                .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
                .anyMatch(m -> m.equals("name must not be blank"))
        );
        assertTrue(violations.stream()
                .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
                .anyMatch(m -> m.equals("description must not be blank"))
        );
        assertTrue(violations.stream()
                .map(v -> v.getPropertyPath().toString() + " " + v.getMessage())
                .anyMatch(m -> m.equals("available must not be null"))
        );
    }
}
