package ru.practicum.shareit.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.util.model.Violation;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Violation handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String msg = (Objects.equals(e.getRequiredType(), BookingState.class))
                ? "Unknown state: "
                : "Unknown argument: ";

        return new Violation(msg + e.getValue());
    }
}
