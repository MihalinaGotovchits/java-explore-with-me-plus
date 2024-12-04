package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**данные для создания новой подборки событий*/
public class NewCompilationDto {

    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    private Set<Long> events;
}