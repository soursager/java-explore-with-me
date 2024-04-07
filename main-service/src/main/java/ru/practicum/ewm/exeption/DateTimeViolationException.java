package ru.practicum.ewm.exeption;

public class DateTimeViolationException extends RuntimeException {
    public DateTimeViolationException(String message) {
        super(message);
    }
}
