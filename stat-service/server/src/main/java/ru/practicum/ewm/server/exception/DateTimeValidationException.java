package ru.practicum.ewm.server.exception;

public class DateTimeValidationException extends RuntimeException {
    public DateTimeValidationException(String message) {
        super(message);
    }
}
