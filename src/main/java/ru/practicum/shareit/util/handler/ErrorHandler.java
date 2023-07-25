package ru.practicum.shareit.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.DuplicateEmailException;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Integer> handleNotFoundException(final NotFoundException e) {
        log.error("Объект не найден. {}", e.getMessage(), e);
        return Map.of("NOT_FOUND", HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Integer> handleNoAccessException(final NoAccessException e) {
        log.error("Ошибка доступа. {}", e.getMessage(), e);
        return Map.of("FORBIDDEN", HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Integer> handleDuplicateEmailException(final DuplicateEmailException e) {
        log.error("Конфликт между запросом пользователя и сервером. {}", e.getMessage(), e);
        return Map.of("CONFLICT", HttpStatus.CONFLICT.value());
    }
}