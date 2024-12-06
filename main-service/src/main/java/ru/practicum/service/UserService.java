package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest user);

    List<UserDto> getListUsers(List<Long> userIds, Integer from, Integer size);

    void deleteUserById(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long requestId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}