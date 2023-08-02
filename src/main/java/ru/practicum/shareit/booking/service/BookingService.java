package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDtoResponse add(long bookerId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse update(long ownerId, long bookingId, boolean approved);

    BookingDtoResponse findAny(long ownerId, long bookingId);

    Collection<BookingDtoResponse> readAllBookingsBooker(long bookerId, BookingState state);

    Collection<BookingDtoResponse> readAllBookingsOwner(long ownerId, BookingState state);
}
