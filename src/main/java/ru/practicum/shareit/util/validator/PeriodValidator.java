package ru.practicum.shareit.util.validator;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.util.exception.BookingUnavailableException;

import java.time.LocalDateTime;

public class PeriodValidator {
    public static void StartAndEndTimeValidation(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getStart() == null) {
            throw new BookingUnavailableException("Отсутствует дата начала бронирование.");
        }
        if (bookingDtoRequest.getEnd() == null) {
            throw new BookingUnavailableException("Отсутствует дата окончания бронирования.");
        }

        if (bookingDtoRequest.getEnd().equals(LocalDateTime.now())
                || bookingDtoRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingUnavailableException("Ошибка в дате окончания бронирования (указана дата сейчас или в прошлом).");
        }

        if (bookingDtoRequest.getStart().equals(LocalDateTime.now())
                || bookingDtoRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingUnavailableException("Ошибка в дате начала бронирования (указана дата сейчас или в прошлом).");
        }

        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BookingUnavailableException("Дата окончания бронирования не может быть раньше начала бронирования.");
        }

        if (bookingDtoRequest.getEnd().equals(bookingDtoRequest.getStart())) {
            throw new BookingUnavailableException("Дата окончания бронирования и начала бронирования одинаковые.");
        }
    }
}
