package ru.practicum.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.SearchEventParamAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventFromAdmin(SearchEventParamAdmin searchEventParamAdmin);

    EventFullDto updateEventFromAdmin(Long userId, UpdateEventAdminRequest updateEventAdminRequest);
}
