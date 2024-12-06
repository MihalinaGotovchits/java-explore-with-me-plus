package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import ru.practicum.status.error.ErrorStatus;

import java.time.LocalDateTime;

@Getter
public abstract class BaseException extends RuntimeException {
    protected final ErrorStatus status;

    protected final String reason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected final LocalDateTime timestamp;

    protected BaseException(ErrorStatus errorStatus, String reason, String message) {
        super(message);
        this.status = errorStatus;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }
}