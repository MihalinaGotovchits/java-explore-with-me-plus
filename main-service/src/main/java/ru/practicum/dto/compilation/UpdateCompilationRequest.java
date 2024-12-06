package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**данные для изменения информации о подборке событий*/
public class UpdateCompilationRequest {
    /**список идентификаторов событий, которые принадлежат данной подборке.
     * Используется для указания того, какие конкретные события должны быть добавлены или заменены в подборке*/
    private Long id;

    private List<Long> events;

    private Boolean pinned;

    @Size(max = 50)
    private String title;
}
