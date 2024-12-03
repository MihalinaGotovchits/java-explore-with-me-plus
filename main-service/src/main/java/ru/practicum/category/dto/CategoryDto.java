package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.utils.Create;
import ru.practicum.utils.Update;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**описание категории события с идентификатором и названием*/
public class CategoryDto {

    private Long id;

    @NotBlank(groups = {Create.class, Update.class})
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    private String name;
}
