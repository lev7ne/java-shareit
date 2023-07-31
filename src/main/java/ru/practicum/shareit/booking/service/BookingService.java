package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDtoResponse add(long bookerId, BookingDto bookingDto);

    BookingDtoResponse update(long ownerId, long bookingId, boolean approved);

    BookingDtoResponse findAny(long ownerId, long bookingId);

    Collection<BookingDtoResponse> readAllBookingsBooker(long bookerId, BookingState state);

    Collection<BookingDtoResponse> readAllBookingsOwner(long ownerId, BookingState state);
}
