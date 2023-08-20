package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDtoResponse add(long bookerId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse update(long ownerId, long bookingId, boolean approved);

    BookingDtoResponse find(long ownerId, long bookingId);

    List<BookingDtoResponse> readAllBookingsBooker(long bookerId, BookingState state, Integer from, Integer size);

    List<BookingDtoResponse> readAllBookingsOwner(long ownerId, BookingState state, Integer from, Integer size);
}
