package ru.practicum.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
/** данные для добавления новой категории*/
public class NewCategoryDto {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;
}
