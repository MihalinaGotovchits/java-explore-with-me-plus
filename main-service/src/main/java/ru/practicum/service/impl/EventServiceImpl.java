package ru.practicum.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.UpdatedRequestsDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UncorrectedParametersException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.service.EventService;
import ru.practicum.status.event.AdminEventStatus;
import ru.practicum.status.event.State;
import ru.practicum.status.event.UserEventStatus;
import ru.practicum.status.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final LocationRepository locationRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    public UpdatedRequestsDto confirmRequestsPrivate(Long userId, Long eventId, EventRequestStatusUpdateRequest updatedRequests) {

        User user = checkUser(userId);

        Event event = checkEvent(eventId);

        checkEventInitiator(event, user);

        List<Long> ids = updatedRequests.getRequestIds().stream().toList();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return RequestMapper.toUpdatedRequestsDto(RequestMapper.toListRequestDto(requestRepository.findAllByIdIn(ids)), List.of());
        }

        List<Request> requests = requestRepository.findAllByEventIdAndStatusAndIdIn(eventId, RequestStatus.PENDING, ids);

        if (requests.size() != updatedRequests.getRequestIds().size()) {
            throw new ConflictException("Изменить можно только заявки в статусе ожидания");
        }

        List<Request> confirmedRequests = getConfirmedRequests(eventId);

        if (confirmedRequests.size() + updatedRequests.getRequestIds().size() > event.getParticipantLimit()) {
            throw new ConflictException("Нельзя выходить за лимиты заявок");
        }

        requestRepository.updateRequestsStatusByIds(updatedRequests.getStatus(), ids);

        if (confirmedRequests.size() + updatedRequests.getRequestIds().size() == event.getParticipantLimit()) {
            requestRepository.rejectRequestsPending();
        }

        entityManager.clear();

        if (updatedRequests.getStatus().equals(RequestStatus.CONFIRMED)) {
            return RequestMapper.toUpdatedRequestsDto(RequestMapper.toListRequestDto(requestRepository.findAllByIdIn(ids)), List.of());
        } else {
            return RequestMapper.toUpdatedRequestsDto(List.of(), RequestMapper.toListRequestDto(requestRepository.findAllByIdIn(ids)));
        }
    }


    @Override
    public EventFullDto getEventPrivate(Long userId, Long eventId) {

        User user = checkUser(userId);

        Event event = checkEvent(eventId);

        checkEventInitiator(event, user);

        return EventMapper.toEventFullDto(event, getConfirmedRequests(event.getId()));

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

    @Override
    public EventFullDto updateEventFromUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {

        User user = checkUser(userId);

        Event oldEvent = checkEvent(eventId);

        checkEventInitiator(oldEvent, user);

        if (!oldEvent.getState().equals(State.PENDING) && !oldEvent.getState().equals(State.CANCELED)) {
            throw new ConflictException("Изменить можно только неподтвержденное событие");
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
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new UncorrectedParametersException("Некорректные параметры даты. Дата начала изменяемого" +
                        "события должна быть не ранне, чем за 2 часа от даты его публикации.");
            }
            eventForUpdate.setEventDate(updateEvent.getEventDate());
            hasChanges = true;
        }
        UserEventStatus action = updateEvent.getStateAction();
        if (action != null) {
            if (UserEventStatus.SEND_TO_REVIEW.equals(action)) {
                eventForUpdate.setState(State.PENDING);
                hasChanges = true;
            } else if (UserEventStatus.CANCEL_REVIEW.equals(action)) {
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

    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, SearchEventParamPrivate searchEventParamPrivate) {

        User user = checkUser(userId);

        PageRequest page = PageRequest.of(searchEventParamPrivate.getFrom() / searchEventParamPrivate.getSize(),
                searchEventParamPrivate.getSize());
        Specification<Event> specification = Specification.where(null);

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("initiator"), user));

        Page<Event> events = eventRepository.findAll(specification, page);

        List<EventShortDto> result = events.getContent().stream()
                .map(EventMapper::toEventShortDto).collect(Collectors.toList());
        Map<Long, List<Request>> confirmedRequestsCount = getConfirmedRequestsCount(events.toList());
        for (EventShortDto event : result) {
            List<Request> requests = confirmedRequestsCount.getOrDefault(event.getId(), List.of());
            event.setConfirmedRequest(requests.size());
        }

        return result;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEvent) {

        User user = checkUser(userId);

        checkEventDate(newEvent.getEventDate());

        Location location = locationRepository.save(LocationMapper.toLocation(newEvent.getLocation()));

        Event event = EventMapper.toEvent(newEvent, user, location, checkCategory(newEvent.getCategory()));
        event.setState(State.PENDING);

        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsPrivate(Long userId, Long eventId) {

        User user = checkUser(userId);

        Event event = checkEvent(eventId);

        checkEventInitiator(event, user);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return RequestMapper.toListRequestDto(requests);
    }

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

    private void checkEventInitiator(Event event, User user) {
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("Событие не создано текущим пользователем");
        }
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

    private User checkUser(Long userId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с Id: " + userId + " не найден");
        } else {
            return user.get();
        }
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new UncorrectedParametersException("Дата события не может быть раньше текущей даты");
        }
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

    private List<Request> getConfirmedRequests(Long eventId) {
        return requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
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
        Long category = updateEvent.getCategory();
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
            oldEvent.setLocation(locationRepository.save(location));
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
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
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
