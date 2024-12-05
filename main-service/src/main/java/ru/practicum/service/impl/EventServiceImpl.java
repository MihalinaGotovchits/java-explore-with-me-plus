package ru.practicum.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
    public List<EventShortDto> getEvents(String text,
                                         List<Integer> categories,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         Integer from,
                                         Integer size) {
        Sort s = Sort.by(Sort.Direction.DESC, sort);
        Pageable pageable = PageRequest.of(from, size, s);

        if (rangeStart == null && rangeEnd == null) {
            return eventRepository.findAllByTextAndCategoryOnlyAvailable(
                            text, categories, paid, String.valueOf(LocalDateTime.now()), onlyAvailable, pageable).stream()
                    .map(EventMapper::toEventShortDto)
                    .toList();
        }
        return eventRepository.findAllByTextAndCategoryInRangeOnlyAvailable(
                        text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable).stream()
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