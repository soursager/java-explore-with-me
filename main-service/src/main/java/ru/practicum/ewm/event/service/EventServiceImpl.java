package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exeption.*;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.request.dto.*;
import ru.practicum.ewm.request.model.*;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.util.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository repository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient client;
    private final ObjectMapper objectMapper;

    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        Event event = EventMapper.toEvent(newEventDto);
        checkEventDateTime(event);

        User initiator = userService.returnIfExists(userId);
        Category category = categoryService.returnIfExists(newEventDto.getCategory());

        event.setInitiator(initiator);
        event.setCategory(category);
        event = repository.save(event);

        log.info("Сохранение события - {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = returnIfExists(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие по id - " + event +  " не найдено");
        }

        addStats(request);

        Map<Long, Long> statsMap = getViewsByEvents(List.of(event));
        event.setViews(statsMap.get(eventId));

        repository.save(event);

        log.info("Получение события по id - {} - {}", eventId, event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(SearchParameters params, HttpServletRequest request) {
        checkParamDateTime(params.getRangeStart(), params.getRangeEnd());

        PageRequest page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        addStats(request);

        Specification<Event> spec = Specification
                .where(SearchSpecs.searchByText(params.getText()))
                .and(SearchSpecs.searchCategories(params.getCategories()))
                .and(SearchSpecs.isPaid(params.getPaid()))
                .and(SearchSpecs.rangeStart(params.getRangeStart()))
                .and(SearchSpecs.rangeEnd(params.getRangeEnd()))
                .and(SearchSpecs.onlyAvailable(params.isOnlyAvailable()))
                .and(SearchSpecs.sortBy(params.getSort()));


        List<Event> events = repository.findAll(spec, page).getContent();
        Map<Long, Long> views = getViewsByEvents(events);

        events = events.stream()
                .peek(e -> {
                    e.setViews(views.getOrDefault(e.getId(), 0L));
                })
                .collect(Collectors.toList());

        log.info("Получение всех событий - {}", events);
        return repository.saveAll(events).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(SearchAdminParameters params) {
        checkParamDateTime(params.getRangeStart(), params.getRangeEnd());

        PageRequest page = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Specification<Event> spec = Specification
                .where(SearchSpecs.searchByUserIds(params.getUsers()))
                .and(SearchSpecs.searchStates(params.getStates()))
                .and(SearchSpecs.searchCategories(params.getCategories()))
                .and(SearchSpecs.rangeStart(params.getRangeStart()))
                .and(SearchSpecs.rangeEnd(params.getRangeEnd()));

        List<Event> events = repository.findAll(spec, page).getContent();

        log.info("Получение всех событий администратором - {} по параметрам - {}", events, params);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, int from, int size) {
        userService.checkExistingUser(userId);
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = repository.findAllByInitiatorId(userId, page);

        log.info("Получение событий - {}", events);
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdAndUserId(Long eventId, Long userId) {
        Event event = checkEventBelongUser(eventId, userId);

        log.info("Получение события - {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public void updateEvent(Event event) {
        repository.save(event);
    }

    @Override
    public EventFullDto updateEventByIdAndUserId(Long eventId, Long userId,
                                                 UpdateEventUserRequest updateEventUserRequest) {
        Event event = checkEventBelongUser(eventId, userId);
        updateEvent(event, updateEventUserRequest, false);

        log.info("Обновление события - {}", event);
        return EventMapper.toEventFullDto(repository.save(event));

    }

    @Override
    public EventFullDto updateEventByIdAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = returnIfExists(eventId);
        updateEvent(event, updateRequest, true);

        log.info("Обновление события администратором по id - {} - {}", eventId, updateRequest);
        return EventMapper.toEventFullDto(repository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, Long eventId) {
        checkEventBelongUser(eventId, userId);
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);

        log.info("Получение всех зпросов на участие по id - {} - {}", eventId, requests);
        return requests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatusByUserAndEvent(Long userId, Long eventId,
                                                                             EventRequestStatusUpdateRequest updateRequest) {
        Event event = checkEventBelongUser(eventId, userId);
        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        int countConfirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, State.CONFIRMED);
        int countParticipantLimit = event.getParticipantLimit();

        List<ParticipationRequest> confirmedRequests = getRequestsByStatus(requests, State.CONFIRMED);
        List<ParticipationRequest> rejectedRequests = getRequestsByStatus(requests, State.REJECTED);

        List<ParticipationRequest> updateRequests = getUpdateRequests(requests, updateRequest.getRequestIds());

        switch (updateRequest.getStatus()) {
            case CONFIRMED:
                if (countConfirmedRequests == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
                    throw new OverLimitException("Достигнут лимит на участие в событии");
                }

                for (ParticipationRequest request : updateRequests) {
                    if (countConfirmedRequests != countParticipantLimit) {
                        request.setStatus(State.CONFIRMED);
                        countConfirmedRequests++;
                        confirmedRequests.add(request);
                    } else {
                        request.setStatus(State.REJECTED);
                        rejectedRequests.add(request);
                    }
                }
                break;
            case REJECTED:
                updateRequests.forEach(r -> {
                    r.setStatus(State.REJECTED);
                    rejectedRequests.add(r);
                });
        }

        event.setConfirmedRequests(countConfirmedRequests);
        repository.save(event);
        requestRepository.saveAll(updateRequests);

        log.info("Обновление запросов пользователя по id - {} по событию с id - {} - {}", eventId, eventId,
                updateRequest);
        return new EventRequestStatusUpdateResult(
                ParticipationRequestMapper.toParticipationRequestDtos(confirmedRequests),
                ParticipationRequestMapper.toParticipationRequestDtos(rejectedRequests));
    }

    @Override
    public Event returnIfExists(Long eventId) {
        return repository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие по id - " + eventId +
                " не найдено"));
    }

    private void checkEventDateTime(Event event) {
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new DateTimeViolationException("Начало события должно быть не раньше, " +
                    "чем за 2 часа от текущего времени");
        } else if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new DateTimeViolationException("Начало события не может быть в прошлом");
        }
    }

    private void checkParamDateTime(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            if (end.isBefore(start)) {
                throw new DateTimeViolationException("Время окончания не может быть раньше даты начала");
            }
        }
    }

    private Event checkEventBelongUser(Long eventId, Long userId) {
        userService.checkExistingUser(userId);
        Event event = returnIfExists(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotOwnerException("Пользователь по id - " + userId + " не является содателем события по id - " +
                    userId);
        }
        return event;
    }

    private void updateEvent(Event event, UpdateEventUserRequest updateRequest, boolean isAdmin) {

        if (event.getState() == State.PUBLISHED) {
            throw new ConflictParameterException("Событие уже опубликовано и не может быть изменено");
        }

        if (updateRequest.getEventDate() != null && !isAdmin &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictParameterException("Начало события должно быть не раньше, " +
                    "чем за 2 часа от текущего времени");
        } else if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictParameterException("Начало события должно быть не раньше, " +
                    "чем за 1 час от текущего времени");
        } else if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        event.setParticipantLimit(updateRequest.getParticipantLimit() != null ?
                updateRequest.getParticipantLimit() : event.getParticipantLimit());

        event.setAnnotation(updateRequest.getAnnotation() != null ?
                updateRequest.getAnnotation() : event.getAnnotation());

        event.setCategory(updateRequest.getCategory() != null ?
                categoryService.returnIfExists(updateRequest.getCategory()) : event.getCategory());

        event.setDescription(updateRequest.getDescription() != null ?
                updateRequest.getDescription() : event.getDescription());

        event.setLocation(updateRequest.getLocation() != null ?
                updateRequest.getLocation() : event.getLocation());

        event.setPaid(updateRequest.getPaid() != null ?
                updateRequest.getPaid() : event.isPaid());

        event.setRequestModeration(updateRequest.getRequestModeration() != null ?
                updateRequest.getRequestModeration() : event.isRequestModeration());

        event.setTitle(updateRequest.getTitle() != null ?
                updateRequest.getTitle() : event.getTitle());

        // Проверка и изменение статусов
        if (updateRequest.getStateAction() != null && isAdmin) {
            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != State.PENDING) {
                        throw new ConflictParameterException("Данное событие не готово к публикации");
                    }
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
            }
        } else if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
            }
        }
    }

    private List<ParticipationRequest> getRequestsByStatus(List<ParticipationRequest> requests, State status) {
        return requests.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    private List<ParticipationRequest> getUpdateRequests(List<ParticipationRequest> requests, List<Long> requestIds) {
        return requests.stream()
                .filter(r -> requestIds.contains(r.getId()))
                .peek(r -> {
                    if (r.getStatus() != State.PENDING) {
                        throw new ConflictParameterException("Зпрос должен иметь статус PENDING");
                    }
                })
                .collect(Collectors.toList());
    }

    private void addStats(HttpServletRequest request) {

        client.createHit("ewm-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now().format(FORMATTER));
    }

    private Map<Long, Long> getViewsByEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> ("/events/" + event.getId()))
                .collect(Collectors.toList());

        LocalDateTime firstDate = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Map<Long, Long> viewStatsMap = new HashMap<>();

        if (firstDate != null) {

            ResponseEntity<List<ViewStatsDto>> response = client.getStats(firstDate.format(FORMATTER),
                    LocalDateTime.now().format(FORMATTER), uris, true);

            List<ViewStatsDto> viewStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });

            viewStatsMap = viewStatsList.stream()
                    .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(
                            statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                            ViewStatsDto::getHits
                    ));
            log.info("Статистика вернулась - {}", viewStatsList);
        }
        return viewStatsMap;
    }
}
