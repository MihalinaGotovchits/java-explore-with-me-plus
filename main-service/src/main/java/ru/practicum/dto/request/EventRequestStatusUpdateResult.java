package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**предназначен для предоставления результата обновления статуса запросов на участие в событии*/
public class EventRequestStatusUpdateResult {
    /**Список объектов ParticipationRequestDto - предоставляют запросы на участие,
     * которые были подтверждены. Это позволяет возвращать пользователю список тех заявок,
     * которые были успешно обработаны и признаны действительными для участия в событии*/
    private List<ParticipationRequestDto> confirmedRequests;
    /**Список объектов ParticipationRequestDto - предоставляют запросы, которые были отклонены.
     * Это позволяет информировать пользователя о статусе их заявок,
     * а также предоставляет возможность отслеживать, какие из них не могут быть выполнены, и по какой причине*/
    private List<ParticipationRequestDto> rejectedRequests;
}
