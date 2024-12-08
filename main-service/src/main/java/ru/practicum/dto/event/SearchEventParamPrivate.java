package ru.practicum.dto.event;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchEventParamPrivate {

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;
}
