package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.UserService;
import ru.practicum.status.request.RequestStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RequestRepository requestRepository;

    /** TODO Доделать проверки */
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Request request = new Request();
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long requestId) {
        return requestRepository.getRequestsByRequester_Id(requestId);
    }

    /** TODO Доделать выполнение ошибок!*/
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElse(null);

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}