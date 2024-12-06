package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<ParticipationRequestDto> getRequestsByRequester_Id(Long requestId);
    Request getRequestByIdAndRequester_Id(Long requestId, Long requesterId);
}