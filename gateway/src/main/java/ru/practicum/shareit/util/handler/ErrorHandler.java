package ru.practicum.shareit.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.exception.UnavailableException;
import ru.practicum.shareit.util.model.Violation;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Violation handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String msg = (Objects.equals(e.getRequiredType(), BookingState.class))
                ? "Unknown state: "
                : "Unknown argument: ";
        return new Violation(msg + e.getValue());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, UnavailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleUnavailableException(final Exception e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return Map.of("BAD_REQUEST", HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Integer> handleThrowable(final Throwable e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return Map.of("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
