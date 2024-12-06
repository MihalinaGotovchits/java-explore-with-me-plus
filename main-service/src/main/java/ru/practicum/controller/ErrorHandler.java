package ru.practicum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.error.ErrorResponse;
import ru.practicum.exception.DataIntegrityViolationException;
import ru.practicum.status.error.ErrorStatus;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        return ErrorResponse.builder()
                .status(exception.getStatus())
                .reason(exception.getReason())
                .message(exception.getMessage())
                .timestamp(exception.getTimestamp())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private ErrorResponse handleThrowable(final RuntimeException e) {
        return ErrorResponse.builder()
                .status(ErrorStatus.FATAL_ERROR)
                .reason("Unexpected reason")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}