package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.util.exception.UnavailableStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState getBookingState(String state) {
        for (BookingState value : values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        throw new UnavailableStateException("UNSUPPORTED_STATUS");
    }
}
