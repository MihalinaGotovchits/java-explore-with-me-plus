package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**предназначен для передачи данных о пользователе с правами администратора*/
public class AdminUserDto {

    private Long id;

    private String name;

    private String email;
}
