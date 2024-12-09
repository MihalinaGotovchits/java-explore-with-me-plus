package ru.practicum.service;

import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.UpdatedRequestsDto;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventFromAdmin(SearchEventParamAdmin searchEventParamAdmin);

    EventFullDto updateEventFromAdmin(Long userId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEventsOfUser(Long userId, SearchEventParamPrivate searchEventParamPrivate);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventPrivate(Long userId, Long eventId);

    EventFullDto updateEventFromUser(Long userId, Long eventId, UpdateEventUserRequest inputEventUpdate);

    List<ParticipationRequestDto> getRequestsPrivate(Long userId, Long eventId);

    UpdatedRequestsDto confirmRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest updatedRequests);

    List<EventShortDto> getEvents(EventParam eventParam);

    EventShortDto getEventById(Long id);
}
