package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class BookingAnswerDtoTest {
    @Autowired
    private JacksonTester<BookingAnswerDto> json;

    @Test
    public void testSerializeDto() throws Exception {

        LocalDateTime start = LocalDateTime.parse("2023-04-18T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse("2023-04-19T16:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        User user = new User();
        user.setId(1L);
        user.setName("User1");
        user.setEmail("test1@ya.ru");

        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("test@ya.ru")
                .build();

        ItemDto testItem = ItemDto.builder()
                .id(1L)
                .name("test Item")
                .owner(user)
                .lastBooking(null)
                .nextBooking(null)
                .available(true)
                .requestId(1L)
                .build();

        BookingAnswerDto testDto = BookingAnswerDto.builder()
                .id(1L)
                .booker(owner)
                .item(testItem)
                .start(start)
                .end(end)
                .build();

        String jsonOutput = json.write(testDto).getJson();
        String expectedJson = "{\"id\":1,\"start\":\"2023-04-18T15:00:00\",\"end\":\"2023-04-19T16:30:00\"," +
                "\"item\":{\"id\":1,\"name\":\"test Item\",\"description\":null,\"available\":true,\"owner\":{\"id\":1," +
                "\"name\":\"User1\",\"email\":\"test1@ya.ru\"},\"requestId\":1,\"lastBooking\":null,\"nextBooking\":null}," +
                "\"booker\":{\"id\":1,\"name\":\"Alex\",\"email\":\"test@ya.ru\"},\"status\":null}";

        assertThat(jsonOutput).isEqualTo(expectedJson);
    }

    @Test
    public void testDeserializeDto() throws Exception {
        String jsonInput = "{\"id\":1,\"start\":\"2023-04-18T15:00:00\",\"end\":\"2023-04-19T16:30:00\"," +
                "\"item\":{\"id\":1,\"name\":\"test Item\",\"description\":null,\"available\":true,\"owner\":{\"id\":1," +
                "\"name\":\"User1\",\"email\":\"test1@ya.ru\"},\"requestId\":1,\"lastBooking\":null,\"nextBooking\":null}," +
                "\"booker\":{\"id\":1,\"name\":\"Alex\",\"email\":\"test@ya.ru\"},\"status\":null}";

        LocalDateTime start = LocalDateTime.parse("2023-04-18T15:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse("2023-04-19T16:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        User user = new User();
        user.setId(1L);
        user.setName("User1");
        user.setEmail("test1@ya.ru");

        UserDto owner = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("test@ya.ru")
                .build();

        ItemDto testItem = ItemDto.builder()
                .id(1L)
                .name("test Item")
                .owner(user)
                .lastBooking(null)
                .nextBooking(null)
                .available(true)
                .requestId(1L)
                .build();

        BookingAnswerDto testDto = BookingAnswerDto.builder()
                .id(1L)
                .booker(owner)
                .item(testItem)
                .start(start)
                .end(end)
                .build();

        BookingAnswerDto dto = json.parse(jsonInput).getObject();

        assertThat(dto.getStart()).isEqualTo(testDto.getStart());
        assertThat(dto.getEnd()).isEqualTo(testDto.getEnd());
        assertThat(dto.getBooker()).isEqualTo(testDto.getBooker());
    }

}
