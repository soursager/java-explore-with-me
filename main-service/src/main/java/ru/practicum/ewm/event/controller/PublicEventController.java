package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.util.SearchParameters;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

    private final EventService service;

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable @Positive Long eventId, HttpServletRequest request) {
        log.info("Публичный запрос события по id - {}", eventId);
        return service.getEventById(eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@Valid SearchParameters params,
                                         HttpServletRequest request) {
        log.info("Публичный запрос событий с параметрами - {}", params);
        return service.getEvents(params, request);
    }

}
