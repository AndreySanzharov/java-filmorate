package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.model.ErrorResponse;


@Slf4j
@RestControllerAdvice
public class ErrorController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(final NotFoundException notFoundException) {
        log.error("Ошибка 404: {}", notFoundException.getMessage(), notFoundException);
        return new ErrorResponse(notFoundException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ErrorResponse handleBadRequestExceptions(final Exception ex) {
        log.error("Ошибка валидации или некорректного запроса: {}", ex.getMessage(), ex);
        return new ErrorResponse("Некорректные параметры: " + ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(final Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ErrorResponse("An unexpected error occurred. Please try again later.");
    }
}
