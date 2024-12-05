package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.EventService;

import java.util.List;

/**
 * Публичный API для работы с событиями
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Slf4j
public class PublicEventController {
    private final EventService eventService;

    /**
     * Список платных (или нет) событий по категориям с текстом в аннотации и описании,
     * в диапазоне времени rangeStart rangeEnd
     * отсортированных по дате или количеству просмотров
     */
    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Integer> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    /**
     * Подробная информация об опубликованном событии по его идентификатору
     *
     * @param id идентификатор события
     * @return краткую информацию о событии
     */
    @GetMapping(path = "/{id}")
    public EventShortDto getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }
}