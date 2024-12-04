package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.status.request.RequestStatus;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**предназначен для обработки запроса на изменение статуса заявок на участие в событии*/
public class EventRequestStatusUpdateRequest {
    /**уникальные идентификаторы заявок на участие*/
    private Set<Long> requestIds;
    /**указывает на новый статус, который необходимо установить для указанных заявок (статусы находятся в enum RequestStatus)*/
    private RequestStatus status;
}
