package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingAnswerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @SneakyThrows
    @Test
    public void testAddBooking() throws Exception {
        BookingAnswerDto bookingAnswerDto = BookingAnswerDto.builder().build();
        BookingDto bookingDto = new BookingDto();

        when(bookingService.addBooking(any(), anyLong())).thenReturn(bookingAnswerDto);

        MvcResult mvcResult = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn();

        BookingAnswerDto responseDto = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BookingAnswerDto.class);

        assertEquals(bookingAnswerDto, responseDto);
    }

    @SneakyThrows
    @Test
    public void testUpdateStatus() {
        BookingAnswerDto bookingAnswerDto = BookingAnswerDto.builder().build();
        BookingDto bookingDto = new BookingDto();
        String approved = "true";

        when(bookingService.updateStatus(1L, 2L, approved)).thenReturn(bookingAnswerDto);

        MvcResult mvcResult = mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", approved)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andReturn();

        BookingAnswerDto responseDto = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BookingAnswerDto.class);

        assertEquals(bookingAnswerDto, responseDto);
    }

    @SneakyThrows
    @Test
    public void testGetBooking() {
        mockMvc.perform(get("/bookings/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(bookingService).getBookingByID(1L, 1L);
    }

    @Test
    public void testGetAllForOwner() throws Exception {
        String state = "ALL";
        Integer from = 0;
        Integer size = 5;
        List<BookingAnswerDto> bookings = Arrays.asList(BookingAnswerDto.builder().build(), BookingAnswerDto.builder().build());

        when(bookingService.getAllForOwner(eq(1L), eq(state), eq(from), eq(size))).thenReturn(bookings);

        MvcResult mvcResult = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingAnswerDto> responseDtoList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        verify(bookingService).getAllForOwner(1L, state, from, size);
        assertEquals(bookings, responseDtoList);
    }

    @Test
    public void getAllWithState() throws Exception {
        String state = "ALL";
        Integer from = 0;
        Integer size = 5;
        List<BookingAnswerDto> bookings = Arrays.asList(BookingAnswerDto.builder().build(), BookingAnswerDto.builder().build());

        when(bookingService.getAllByState(anyLong(), eq(state), eq(from), eq(size))).thenReturn(bookings);

        MvcResult mvcResult = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingAnswerDto> responseDtoList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        verify(bookingService, atLeastOnce()).getAllByState(1L, state, from, size);
        assertEquals(bookings, responseDtoList);
    }

}
