package ru.practicum.shareit.util.exception;

public class EmptyEmailException extends RuntimeException {
    public EmptyEmailException() {
    }

    public EmptyEmailException(String message) {
        super(message);
    }
}