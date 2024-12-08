package ru.practicum.service;

import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventFromAdmin(SearchEventParamAdmin searchEventParamAdmin);

    EventFullDto updateEventFromAdmin(Long userId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEventsOfUser(Long userId, SearchEventParamPrivate searchEventParamPrivate);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto updateEventFromUser(Long userId, Long eventId, UpdateEventUserRequest inputEventUpdate);

    List<ParticipationRequestDto> getRequestsPrivate(Long userId, Long eventId);

    List<ParticipationRequestDto> confirmRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest updatedRequests);
}
