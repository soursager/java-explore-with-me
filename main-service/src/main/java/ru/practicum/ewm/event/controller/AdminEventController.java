package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.util.SearchAdminParameters;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService service;

    @GetMapping
    public List<EventFullDto> getEvents(@Valid SearchAdminParameters params) {
        log.info("Запрос админа на предоставление событий по параметрам - {}", params);
        return service.getEventsByAdmin(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@RequestBody @Valid UpdateEventAdminRequest updateRequest,
                                    @PathVariable @Positive Long eventId) {
        log.info("Запрос администратором на изменение события по id - {} - {}", eventId, updateRequest);
        return service.updateEventByIdAdmin(eventId, updateRequest);
    }
}
