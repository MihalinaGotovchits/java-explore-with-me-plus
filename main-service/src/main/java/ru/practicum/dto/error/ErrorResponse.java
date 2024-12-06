package ru.practicum.dto.error;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.status.error.ErrorStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private ErrorStatus status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}