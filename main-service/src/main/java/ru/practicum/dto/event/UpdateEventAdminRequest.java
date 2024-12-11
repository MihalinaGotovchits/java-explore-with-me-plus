package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.Location;
import ru.practicum.status.event.AdminEventStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventAdminRequest {

    private Long id;

    @Size(min = 3, max = 120)
    private String title;

    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long categoryId;

    private Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Length(min = 20, max = 7000)
    private String description;

    private UserDto initiator;

    @PositiveOrZero
    private Integer participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private AdminEventStatus stateAction;

    @Valid
    private Location location;
}