package ru.practicum.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.dto.error.ErrorResponse;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataIntegrityViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UncorrectedParametersException;
import ru.practicum.status.error.ErrorStatus;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        return ErrorResponse.builder()
                .status(ErrorStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ErrorResponse handleConflict(final ConflictException e) {
        return ErrorResponse.builder()
                .status(ErrorStatus.FATAL_ERROR)
                .reason("Unexpected reason")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorResponse handleNotFoundException(final NotFoundException e) {
        return ErrorResponse.builder()
                .status(ErrorStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        return ErrorResponse.builder()
                .status(ErrorStatus.FATAL_ERROR)
                .reason("Unexpected reason")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpNotReadableException(HttpMessageNotReadableException e) {
        return ErrorResponse.builder()
                .status(ErrorStatus.FATAL_ERROR)
                .reason("Unexpected reason")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpNotReadableException(UncorrectedParametersException e) {
        return ErrorResponse.builder()
                .status(ErrorStatus.FATAL_ERROR)
                .reason("Unexpected reason")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}