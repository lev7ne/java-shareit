package ru.practicum.shareit.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.*;
import ru.practicum.shareit.util.model.Violation;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({NoAccessException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFoundException(final Exception e) {
        log.error("Произошла ошибка. {}", e.getMessage(), e);
        return Map.of("NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Integer> handleDuplicateEmailException(final DuplicateEmailException e) {
        log.error("Конфликт между запросом пользователя и сервером. {}", e.getMessage(), e);
        return Map.of("CONFLICT", HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleBookingUnavailableException(final BookingUnavailableException e) {
        log.error("Конфликт между запросом пользователя и сервером. {}", e.getMessage(), e);
        return Map.of("BAD_REQUEST", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UnavailableStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Violation handleAnyException(Throwable e) {
        return new Violation(e.getMessage());
    }
}