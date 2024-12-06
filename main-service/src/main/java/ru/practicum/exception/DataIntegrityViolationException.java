package ru.practicum.exception;

import ru.practicum.status.error.ErrorStatus;


public class DataIntegrityViolationException extends BaseException {
    public DataIntegrityViolationException(String message) {
        super(ErrorStatus.CONFLICT, "Integrity constraint has been violated.", message);
    }
}