package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.DataIntegrityViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;
import ru.practicum.status.event.State;
import ru.practicum.status.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    public UserDto createUser(NewUserRequest user) {
        log.info("createUser - invoked");
        User user1 = userRepository.save(UserMapper.toUser(user));
        log.info("createUser - user save successfully - {}", user);
        return UserMapper.userDto(user1);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getListUsers(List<Long> userIds, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        log.info("getAllUsers - successfully");
        return (userIds != null) ? userRepository.findByIdIn(userIds, page).stream()
                .map(UserMapper::userDto).collect(Collectors.toList()) : userRepository.findAll(page)
                .stream().map(UserMapper::userDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        log.info("deleteUserById - successfully - {}", userId);
        userRepository.deleteById(userId);
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d not found", eventId)));

        requestRepository.getRequestByRequester_IdAndEvent_Id(userId, eventId)
                .ifPresent(result -> {
                    throw new NotFoundException(String.format("Request with userId=%d and eventId=%d", userId, eventId));
                });

        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException(
                    "The event initiator cannot add a request to participate in his event.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataIntegrityViolationException(
                    "You can't participate in an unpublished event.");
        }

        if (event.getParticipantLimit() <= requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size()) {
            throw new DataIntegrityViolationException(
                    "The event has reached its participation request limit.");
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(RequestStatus.PENDING)
                .build();

        if (Boolean.FALSE.equals(event.getRequestModeration())) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequestDto requestDto = RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        log.info("createRequest - request save successfully - {}", request);
        return requestDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long requestId) {
        List<ParticipationRequestDto> dtos = requestRepository.getRequestsByRequester_Id(requestId);
        log.info("getRequests - successfully");
        return dtos;
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequestDto requestDto = RequestMapper.toParticipationRequestDto(requestRepository.save(request));
        log.info("cancelRequest - successfully - {}", request);
        return requestDto;
    }
}