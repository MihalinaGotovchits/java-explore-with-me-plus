package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;
import ru.practicum.status.request.RequestStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest user) {
        log.info("createUser - invoked");
        User user1 = userRepository.save(UserMapper.toUser(user));
        log.info("createUser - user save successfully - {}", user);
        return UserMapper.userDto(user1);
    }

    @Override
    @Transactional
    public List<UserDto> getListUsers(List<Long> userIds, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        log.info("getAllUsers - successfully");
        return (userIds != null) ? userRepository.findByIdIn(userIds, page).stream()
                .map(UserMapper::userDto).collect(Collectors.toList()) : userRepository.findAll(page)
                .stream().map(UserMapper::userDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)){
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        log.info("deleteUserById - successfully - {}", userId);
        userRepository.deleteById(userId);
    }

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