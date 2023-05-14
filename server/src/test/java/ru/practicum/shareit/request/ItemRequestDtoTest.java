package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    public void testSerializeDto() throws Exception {
        LocalDateTime created = LocalDateTime.parse("2023-04-24T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();

        String jsonOutput = json.write(testDto).getJson();
        String expectedJson = "{\"id\":1,\"name\":\"test dto name\",\"description\":\"test description\"," +
                "\"requestor\":{\"id\":1,\"name\":\"Test Name\",\"email\":\"newtest@yandex.ru\"}," +
                "\"created\":\"2023-04-24T15:00:00\",\"items\":[]}";

        assertThat(jsonOutput).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserializeDto() throws Exception {
        LocalDateTime created = LocalDateTime.parse("2023-04-24T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String jsonInput = "{\"id\":1,\"name\":\"test dto name\",\"description\":\"test description\"," +
                "\"requestor\":{\"id\":1,\"name\":\"Test Name\",\"email\":\"newtest@yandex.ru\"}," +
                "\"created\":\"2023-04-24T15:00:00\",\"items\":[]}";

        User user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("newtest@yandex.ru");

        ItemRequestDto testDto = ItemRequestDto.builder()
                .id(1L)
                .name("test dto name")
                .description("test description")
                .requestor(user)
                .created(created)
                .items(new ArrayList<>())
                .build();

        ItemRequestDto dto = json.parse(jsonInput).getObject();

        assertThat(dto.getCreated()).isEqualTo(testDto.getCreated());
        assertThat(dto.getName()).isEqualTo(testDto.getName());

    }
}
