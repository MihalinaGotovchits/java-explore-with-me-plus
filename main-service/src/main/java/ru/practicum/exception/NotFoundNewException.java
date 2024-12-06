package ru.practicum.exception;

import ru.practicum.status.error.ErrorStatus;

public class NotFoundNewException extends BaseException {

    public NotFoundNewException(String message) {
        super(ErrorStatus.NOT_FOUND, "The required object was not found.", message);
    }
}