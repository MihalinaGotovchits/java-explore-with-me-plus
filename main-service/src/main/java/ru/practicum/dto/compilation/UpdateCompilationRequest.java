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
class UpdateCompilationRequest {
    /**список идентификаторов событий, которые принадлежат данной подборке.
     * Используется для указания того, какие конкретные события должны быть добавлены или заменены в подборке*/
    private List<Long> events;

    private boolean pinned;

    @Size(max = 50)
    private String title;
}
