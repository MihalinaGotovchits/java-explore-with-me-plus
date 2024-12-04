package ru.practicum.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import ru.practicum.model.Location;
import ru.practicum.status.event.UserEventStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**данные для изменения события от имени пользователя*/
class UpdateEventUserRequest {

    private String annotation;

    private Integer category;

    private String description;

    private String eventDate;

    private Location location;

    private boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private UserEventStatus stateAction;

    private String title;
}
