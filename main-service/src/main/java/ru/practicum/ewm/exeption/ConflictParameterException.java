package ru.practicum.ewm.exeption;

import lombok.Getter;

@Getter
public class ConflictParameterException extends RuntimeException {
    public ConflictParameterException(String message) {
        super(message);
    }
}
