package ru.practicum.shareit.util.validator;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.util.exception.BookingUnavailableException;

import java.time.LocalDateTime;

public class PeriodValidator {
    public static void StartAndEndTimeValidation(BookingDto bookingDto) {
        if (bookingDto.getStart() == null) {
            throw new BookingUnavailableException("Отсутствует дата начала бронирование.");
        }
        if (bookingDto.getEnd() == null) {
            throw new BookingUnavailableException("Отсутствует дата окончания бронирования.");
        }

        if (bookingDto.getEnd().equals(LocalDateTime.now())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BookingUnavailableException("Ошибка в дате окончания бронирования (указана дата сейчас или в прошлом).");
        }

        if (bookingDto.getStart().equals(LocalDateTime.now())
                || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingUnavailableException("Ошибка в дате начала бронирования (указана дата сейчас или в прошлом).");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingUnavailableException("Дата окончания бронирования не может быть раньше начала бронирования.");
        }

        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new BookingUnavailableException("Дата окончания бронирования и начала бронирования одинаковые.");
        }
    }
}
