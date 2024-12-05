package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.SearchEventParamAdmin;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UncorrectedParametersException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.Request;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.service.EventService;
import ru.practicum.status.event.AdminEventStatus;
import ru.practicum.status.event.State;
import ru.practicum.status.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    @Override
    public List<EventFullDto> getAllEventFromAdmin(SearchEventParamAdmin searchEventParamAdmin) {
        PageRequest page = PageRequest.of(searchEventParamAdmin.getFrom() / searchEventParamAdmin.getSize(),
                searchEventParamAdmin.getSize());
        Specification<Event> specification = Specification.where(null);

        List<Long> users = searchEventParamAdmin.getUserIds();
        List<String> states = searchEventParamAdmin.getStates();
        List<Long> categories = searchEventParamAdmin.getCategories();
        LocalDateTime rangeStart = searchEventParamAdmin.getRangeStart();
        LocalDateTime rangeEnd = searchEventParamAdmin.getRangeEnd();

        if (users != null && !users.isEmpty()) {
            specification = specification.and(((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users)));
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("eventStatus").as(String.class).in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        Page<Event> events = eventRepository.findAll(specification, page);

        List<EventFullDto> result = events.getContent().stream()
                .map(EventMapper::toEventFullDto).collect(Collectors.toList());
        Map<Long, List<Request>> confirmedRequestsCount = getConfirmedRequestsCount(events.toList());
        for (EventFullDto event : result) {
            List<Request> requests = confirmedRequestsCount.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequests(requests.size());
        }
        return result;
    }

    @Override
    public EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event oldEvent = checkEvent(eventId);
        if (oldEvent.getState().equals(State.PUBLISHED) || oldEvent.getState().equals(State.CANCELED)) {
            throw new ConflictException("Имзменить можно только неподтвержденное событие");
        }
        boolean hasChanges = false;
        Event eventForUpdate = universalUpdate(oldEvent, EventMapper.toNewEventDto(updateEvent));
        if (eventForUpdate == null) {
            eventForUpdate = oldEvent;
        } else {
            hasChanges = true;
        }
        LocalDateTime eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new UncorrectedParametersException("Некорректные параметры даты. Дата начала изменяемого" +
                        "события должна быть не ранне, чем за час от даты его публикации.");
            }
            eventForUpdate.setEventDate(updateEvent.getEventDate());
            hasChanges = true;
        }
        AdminEventStatus action = updateEvent.getStateAction();
        if (action != null) {
            if (AdminEventStatus.PUBLISH_EVENT.equals(action)) {
                eventForUpdate.setState(State.PUBLISHED);
                hasChanges = true;
            } else if (AdminEventStatus.REJECT_EVENT.equals(action)) {
                eventForUpdate.setState(State.CANCELED);
                hasChanges = true;
            }
        }
        Event eventAfterUpdate = null;
        if (hasChanges) {
            eventAfterUpdate = eventRepository.save(eventForUpdate);
        }
        return eventAfterUpdate != null ? EventMapper.toEventFullDto(eventAfterUpdate) : null;
    }


    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с Id: " + eventId + " не найдено")
        );
    }

    private Category checkCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Категория с Id: " + categoryId + " не найдена")
        );
    }

    /**
     * Метод для получения подсчета подтвержденных запросов на участие в событиях.
     * Возвращает карту, где ключ — идентификатор события, а значение — список запросов
     */
    private Map<Long, List<Request>> getConfirmedRequestsCount(List<Event> events) {
        List<Request> requests = requestRepository.findAllByEventIdInAndStatus(events.stream()
                .map(Event::getId).collect(Collectors.toList()), RequestStatus.CONFIRMED);
        return requests.stream().collect(Collectors.groupingBy(r -> r.getEvent().getId()));
    }

    /**
     * Метод, который выполняет универсальное обновление полей события на основе данных, полученных из DTO NewEventDto.
     * Проверяет, были ли изменения, и обновляет только измененные поля.
     */
    private Event universalUpdate(Event oldEvent, NewEventDto updateEvent) {
        boolean hasChanges = false;
        String annotation = updateEvent.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            oldEvent.setAnnotation(annotation);
            hasChanges = true;
        }
        Long category = updateEvent.getCategoryId();
        if (category != null) {
            Category category1 = checkCategory(category);
            oldEvent.setCategory(category1);
            hasChanges = true;
        }
        String description = updateEvent.getDescription();
        if (description != null && !description.isBlank()) {
            oldEvent.setDescription(description);
            hasChanges = true;
        }
        if (updateEvent.getLocation() != null) {
            Location location = LocationMapper.toLocation(updateEvent.getLocation());
            oldEvent.setLocation(location);
            hasChanges = true;
        }
        Integer participantLimit = updateEvent.getParticipantLimit();
        if (participantLimit != null) {
            oldEvent.setParticipantLimit(participantLimit);
            hasChanges = true;
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
            hasChanges = true;
        }
        if (updateEvent.getRequestModerator() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModerator());
            hasChanges = true;
        }
        String title = updateEvent.getTitle();
        if (title != null && !title.isBlank()) {
            oldEvent.setTitle(title);
            hasChanges = true;
        }
        if (!hasChanges) {
            oldEvent = null;
        }
        return oldEvent;
    }
}
