package ru.practicum.ewm.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final MethodArgumentNotValidException e) {
        log.error("Вызвана ошибка валидации - {}", e.getLocalizedMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({DateTimeViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse dateTimeViolationException(final DateTimeViolationException e) {
        log.error("Вызвана ошибка валидации - {}", e.getLocalizedMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse missingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.error("Вызвана ошибка валидации - {}", e.getLocalizedMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse dataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("Вызвана ошибка уникальности - {}", e.getLocalizedMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(final NotFoundException e) {
        log.error("Вызвана ошибка Значение не найдено - {}", e.getLocalizedMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConflictParameterException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflictParameterException(final ConflictParameterException e) {
        log.error("Вызвана ошибка валидации поля ");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(OverLimitException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse overLimitException(final OverLimitException e) {
        log.error("Достигнут лимит участников по заявке");
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse incorrectParameterException(final Throwable e) {
        log.error("Вызвана ошибка некорректного запроса - {}", e.getLocalizedMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerConflictException(final ConflictException exception) {
        log.info("Статус 409, было нарушено ограничение целостности");
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.info("Статус 400, для запрошенной операции условия не выполнены");
        return new ErrorResponse(e.getMessage());
    }
}
