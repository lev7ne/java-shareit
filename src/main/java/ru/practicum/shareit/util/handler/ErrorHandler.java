package ru.practicum.shareit.util.handler;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.EmptyEmailException;
import ru.practicum.shareit.util.exception.NotFoundException;


import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFoundException(final NotFoundException e) {
        return Map.of("NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Integer> handleValidationException(final EmptyEmailException e) {
        return Map.of("BAD_REQUEST", HttpStatus.BAD_REQUEST.value());
    }
}