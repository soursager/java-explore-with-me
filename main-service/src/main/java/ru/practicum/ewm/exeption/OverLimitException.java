package ru.practicum.ewm.exeption;

public class OverLimitException extends RuntimeException {
    public OverLimitException(String message) {
        super(message);
    }
}
