package ru.practicum.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventParam;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    /**
     * TODO пока что не работает с сортировкой по количеству просмотров
     */
    @Override
    public List<EventShortDto> getEvents(EventParam eventParam) {
        if (eventParam.getRangeStart() == null && eventParam.getRangeEnd() == null) {
            return eventRepository.findAllByTextAndCategoryOnlyAvailable(
                            eventParam.getText(),
                            eventParam.getCategories(),
                            eventParam.getPaid(),
                            String.valueOf(LocalDateTime.now()),
                            eventParam.getOnlyAvailable(),
                            eventParam.getPageable())
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .toList();
        }
        return eventRepository.findAllByTextAndCategoryInRangeOnlyAvailable(
                        eventParam.getText(),
                        eventParam.getCategories(),
                        eventParam.getPaid(),
                        eventParam.getRangeStart(),
                        eventParam.getRangeEnd(),
                        eventParam.getOnlyAvailable(),
                        eventParam.getPageable())
                .stream()
                .map(EventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventShortDto getEventById(Long id) {
        return eventRepository.findById(id)
                .map(EventMapper::toEventShortDto)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }
}