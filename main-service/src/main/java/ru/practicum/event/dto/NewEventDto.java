package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.utils.Update;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**данные для создания нового события*/
public class NewEventDto {
    @Size(min = 20, max = 2000, groups = Update.class)
    private String annotation;

    @NotNull
    @Positive
    private Long categoryId;

    @NotBlank
    @Size(min = 20, max = 7000, groups = Update.class)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModerator;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
