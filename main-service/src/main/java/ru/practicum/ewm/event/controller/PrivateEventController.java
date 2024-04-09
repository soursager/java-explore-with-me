package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                    @PathVariable @Positive Long userId) {
        log.info("Запрос на создание события - {}", newEventDto);
        return service.createEvent(newEventDto, userId);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUserId(@PathVariable @Positive Long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос событий пользователя по id - {} начало - {} размером - {}", userId, from, size);
        return service.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndUserId(@PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long eventId) {
        log.info("Запрос события по id - {} пользователем с id - {}", eventId, userId);
        return service.getEventByIdAndUserId(eventId, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByIdAndUserId(@RequestBody @Valid UpdateEventUserRequest updateEventUserRequest,
                                                 @PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId) {
        log.info("Запрос обновления события по id - {} пользователем с id - {} - {}",
                eventId, userId, updateEventUserRequest);
        return service.updateEventByIdAndUserId(eventId, userId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserAndEvent(@PathVariable @Positive Long userId,
                                                                   @PathVariable @Positive Long eventId) {
        log.info("Запрос получения запросов на участие в событии по id - {} пользователем по id - {}",
                eventId, userId);
        return service.getRequestsByUserAndEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsByUserAndEvent(
            @RequestBody EventRequestStatusUpdateRequest updateRequest,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId) {
        log.info("Запрос на обновления статусов запросов на событие по id - {} пользователем с id - {} - {}",
                eventId, userId, updateRequest);
        return service.updateRequestsStatusByUserAndEvent(userId, eventId, updateRequest);
    }
}
