package ru.practicum.shareit.exceptions;

public class NotBeOwnerException extends RuntimeException {
    public NotBeOwnerException(String message) {
        super(message);
    }
}