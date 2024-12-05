package ru.practicum.service;

import ru.practicum.dto.event.EventParam;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(EventParam eventParam);

    EventShortDto getEventById(Long id);
}