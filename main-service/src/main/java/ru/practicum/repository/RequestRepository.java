package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;
import ru.practicum.status.request.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    /**метод находит все запросы на основе списока идентификаторов событий (eventIds).
     * и status — указывает статус запроса, по которому также будет выполняться фильтрация.
     Метод возвращает список объектов Request — все запросы, которые соответствуют переданным идентификаторам
     событий и указанному статусу.*/
    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    List<ParticipationRequestDto> getRequestsByRequester_Id(Long requestId);

    Request getRequestByIdAndRequester_Id(Long requestId, Long requesterId);
}
