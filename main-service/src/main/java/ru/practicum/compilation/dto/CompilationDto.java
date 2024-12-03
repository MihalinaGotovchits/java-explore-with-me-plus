package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/** подборка событий, содержащая список событий, заголовок и статус закрепления*/
public class CompilationDto {
    private Long id;

    private Set<EventShortDto> events;

    private Boolean pinned;

    private String title;
}
