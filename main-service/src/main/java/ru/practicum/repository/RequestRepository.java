package ru.practicum.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;
import ru.practicum.status.request.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    /**метод находит все запросы на основе списока идентификаторов событий (eventIds).
     * и status — указывает статус запроса, по которому также будет выполняться фильтрация.
     Метод возвращает список объектов Request — все запросы, которые соответствуют переданным идентификаторам
     событий и указанному статусу.*/
    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequestDto> getRequestsByRequester_Id(Long requestId);

    Request getRequestByIdAndRequester_Id(Long requestId, Long requesterId);

    Optional<Request> getRequestByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

    Integer countByEvent_Id(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdAndStatusAndIdIn(Long eventId, RequestStatus status, List<Long> requestIds);

    List<Request> findAllByEventId(Long eventId);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = :newStatus WHERE r.id IN :ids")
    int updateRequestsStatusByIds(@Param("newStatus") RequestStatus newStatus, @Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = 'REJECTED' WHERE r.status = 'PENDING'")
    void rejectRequestsPending();
}