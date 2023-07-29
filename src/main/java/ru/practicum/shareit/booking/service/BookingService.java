package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

public interface BookingService {
    BookingDtoResponse add(long bookerId, BookingDto bookingDto);
}
