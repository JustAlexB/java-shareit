package ru.practicum.shareit.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoGtw;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({SpringExtension.class})
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;
    @Autowired
    private ObjectMapper objectMapper;
    private final String path = "/bookings";
    private final String xUserId = "X-Sharer-User-Id";

    @Test
    public void shouldAddBooking() throws Exception {
        BookingDtoGtw booking = new BookingDtoGtw(9L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(booking, HttpStatus.OK);
        when(bookingClient.bookItem(anyLong(), any(BookingDtoGtw.class))).thenReturn(responseEntity);

        mockMvc.perform(post(path)
                        .header(xUserId, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(booking.getItemId()));
    }

    @Test
    public void shouldGetBooking() throws Exception {
        BookingDtoGtw booking = new BookingDtoGtw(9L, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(booking, HttpStatus.OK);
        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(responseEntity);

        mockMvc.perform(get(path + "/{id}", 1L)
                        .header(xUserId, 1L))
                .andExpect(status().isOk());
        verify(bookingClient, atLeastOnce()).getBooking(1L, 1L);
    }

    @Test
    public void shouldUpdateStatus() throws Exception {
        BookingDtoGtw booking = new BookingDtoGtw(9L, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(booking, HttpStatus.OK);
        when(bookingClient.updateStatus(anyLong(), anyLong(), anyString())).thenReturn(responseEntity);

        mockMvc.perform(patch(path + "/{bookingId}", 1L)
                        .param("approved", "true")
                        .header(xUserId, 2L))
                .andExpect(status().isOk());

        verify(bookingClient, atLeastOnce()).updateStatus(1L, 2L, "true");
    }

    @Test
    public void shouldGetBookings() throws Exception {
        String state = "ALL";
        Integer from = 0;
        Integer size = 5;
        BookingDtoGtw booking = new BookingDtoGtw(9L, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(booking, HttpStatus.OK);
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(get(path)
                        .header(xUserId, 1L)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(bookingClient, atLeastOnce()).getBookings(1L, BookingState.ALL, from, size);
    }

    @Test
    public void shouldGetBookingsForOwner() throws Exception {
        String state = "ALL";
        Integer from = 0;
        Integer size = 5;
        BookingDtoGtw booking = new BookingDtoGtw(9L, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(booking, HttpStatus.OK);
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt())).thenReturn(responseEntity);

        MvcResult mvcResult = mockMvc.perform(get(path + "/owner")
                        .header(xUserId, 1L)
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(bookingClient, atLeastOnce()).getAllForOwner(1L, BookingState.ALL, from, size);
    }
}
