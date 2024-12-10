package ru.practicum.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class UserRequestsController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("Create new request with userId = {}, eventId = {}", userId, eventId);
        return userService.createRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("Get request by id = {}", userId);
        return userService.getRequests(userId);
    }

    @PatchMapping(path = "/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Cancel request with id = {}, requestId = {}", userId, requestId);
        return userService.cancelRequest(userId, requestId);
    }
}