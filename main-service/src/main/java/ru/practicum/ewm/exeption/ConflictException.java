package ru.practicum.ewm.exeption;

public class ConflictException extends RuntimeException{
    public ConflictException(String message) {
        super(message);
    }
}
