package ru.practicum.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}