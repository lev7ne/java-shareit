package ru.practicum.shareit.util.validator;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.util.exception.UnavailableException;

import java.time.LocalDateTime;

public class PeriodValidator {
    public static void StartAndEndTimeValidation(BookingDtoRequest bookingDtoRequest) {
        if (bookingDtoRequest.getStart() == null) {
            throw new UnavailableException("Отсутствует дата начала бронирование.");
        }
        if (bookingDtoRequest.getEnd() == null) {
            throw new UnavailableException("Отсутствует дата окончания бронирования.");
        }

        if (bookingDtoRequest.getEnd().equals(LocalDateTime.now())
                || bookingDtoRequest.getEnd().isBefore(LocalDateTime.now())) {
            throw new UnavailableException("Ошибка в дате окончания бронирования (указана дата сейчас или в прошлом).");
        }

        if (bookingDtoRequest.getStart().equals(LocalDateTime.now())
                || bookingDtoRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new UnavailableException("Ошибка в дате начала бронирования (указана дата сейчас или в прошлом).");
        }

        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new UnavailableException("Дата окончания бронирования не может быть раньше начала бронирования.");
        }

        if (bookingDtoRequest.getEnd().equals(bookingDtoRequest.getStart())) {
            throw new UnavailableException("Дата окончания бронирования и начала бронирования одинаковые.");
        }
    }
}
