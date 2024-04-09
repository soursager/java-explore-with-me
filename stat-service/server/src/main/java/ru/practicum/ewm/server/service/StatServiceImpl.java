package ru.practicum.ewm.server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.Repository.StatRepository;
import ru.practicum.ewm.server.exception.DateTimeValidationException;
import ru.practicum.ewm.server.model.HitMapper;
import ru.practicum.ewm.server.model.Stats;
import ru.practicum.ewm.server.model.StatsMapper;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatRepository statRepository;

    public void createHit(EndpointHitDto endpointHitDto) {
        log.info("Сохранение статистики - {}", endpointHitDto);
        statRepository.save(HitMapper.toHit(endpointHitDto));
    }

    public List<ViewStatsDto> getStats(String startDate, String endDate, List<String> uris, boolean unique) {
        log.info("Запрос статистики с - {} по - {} по uris - {}, уникальность - {}", startDate, endDate, uris, unique);
        LocalDateTime start = LocalDateTime.parse(decodeRequest(startDate), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(decodeRequest(endDate), FORMATTER);

        checkDate(start, end);

        List<Stats> stats;
        if (unique && uris == null) {
            stats = statRepository.findAllUniqueHits(start, end);
        } else if (unique) {
            stats = statRepository.findAllUniqueHitsByUri(start, end, uris);
        } else if (!unique && uris == null) {
            stats = statRepository.findAllHits(start, end);
        } else {
            stats = statRepository.findAllHitsByUri(start, end, uris);
        }
        return stats.stream()
                .map(StatsMapper::toViewStatsDto)
                .collect(Collectors.toList());
    }

    private void checkDate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new DateTimeValidationException("Дата начала не может быть позднее даты окончания");
        } else if (start.isAfter(LocalDateTime.now())) {
            throw new DateTimeValidationException("Дата начала не может быть позднее настоящего времени");
        }
    }

    private String decodeRequest(String request) {
        return URLDecoder.decode(request);
    }
}
