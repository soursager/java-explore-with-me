package ru.practicum.ewm.server.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.server.service.StatService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Запрос на сохранение просмотра - {}", endpointHitDto);
        statService.createHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики с - {} по - {} по адресам - {} уникальность - {}",
                start, end, uris, unique);
        return statService.getStats(start, end, uris, unique);
    }
}
