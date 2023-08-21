package ru.practicum.shareit.util.validator;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.util.exception.UnavailableException;

@UtilityClass
public class Validator {
    public static void startAndEndTimeBookingValidation(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new UnavailableException("Дата окончания бронирования не может быть раньше начала бронирования.");
        }
        if (bookingDtoRequest.getEnd().equals(bookingDtoRequest.getStart())) {
            throw new UnavailableException("Дата окончания бронирования и начала бронирования одинаковые.");
        }
    }
}
