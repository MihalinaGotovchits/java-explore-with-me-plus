package ru.practicum.controller.pub;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.SearchEventParamPublic;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEvents(@Valid @ModelAttribute SearchEventParamPublic searchEventParamPublic) {
        log.info("/admin/events/GET/searchEvents");
        return eventService.getAllEventPublic(searchEventParamPublic);
    }

    @GetMapping("/{id}")
    public EventFullDto getPrivateEvent(@PathVariable @Min(1) Long id) {
        log.info("Get request by id = {}", id);
        return eventService.getEvent(id);
    }
}
