package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.model.BookingState.ALL;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    BookingDtoRequest bookingDtoRequest1;
    BookingDtoRequest bookingDtoRequest2;
    BookingDtoResponse bookingDtoResponse1;
    BookingDtoResponse bookingDtoResponse2;
    @MockBean
    private BookingService mockBookingService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime start = LocalDateTime.of(2023, Month.AUGUST, 25, 10, 10, 10);
        LocalDateTime end = LocalDateTime.of(2023, Month.AUGUST, 26, 10, 10, 10);

        bookingDtoRequest1 = new BookingDtoRequest(start, end, 1);
        bookingDtoRequest2 = new BookingDtoRequest(start, end, 2);

        bookingDtoResponse1 = new BookingDtoResponse(1, start, end, APPROVED, null, null);
        bookingDtoResponse2 = new BookingDtoResponse(2, start, end, APPROVED, null, null);
    }

    @Test
    void createBooking() throws Exception {
        when(mockBookingService.add(anyLong(), any()))
                .thenReturn(bookingDtoResponse1);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is(bookingDtoRequest1.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoRequest1.getEnd().toString())));
    }

    @Test
    void updateApproval() throws Exception {
        when(mockBookingService.update(eq(1L), eq(1L), anyBoolean()))
                .thenReturn(bookingDtoResponse1);

        mockMvc.perform(patch("/bookings/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is(APPROVED.name())));
    }

    @Test
    void readAnyBooking() throws Exception {
        when(mockBookingService.find(eq(1L), eq(1L))).thenReturn(bookingDtoResponse1);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void readAllBookerBookings() throws Exception {
        List<BookingDtoResponse> itemDtoResponseList = Arrays.asList(bookingDtoResponse1, bookingDtoResponse2);
        when(mockBookingService.readAllBookingsBooker(eq(1L), eq(ALL), eq(0), eq(10)))
                .thenReturn(itemDtoResponseList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void readAllOwnerBooking() throws Exception {
        List<BookingDtoResponse> itemDtoResponseList = Arrays.asList(bookingDtoResponse1, bookingDtoResponse2);
        when(mockBookingService.readAllBookingsOwner(eq(1L), eq(ALL), eq(0), eq(10)))
                .thenReturn(itemDtoResponseList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void readAllOwnerBooking_whenInvalidState_thenThrowException() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("state", "ERROR")
                        .queryParam("from", "0")
                        .queryParam("size", "10"))
                .andExpect(status().isInternalServerError());
    }
}