package ru.practicum.ewm.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.Repository.StatRepository;
import ru.practicum.ewm.server.exception.DateTimeValidationException;
import ru.practicum.ewm.server.model.HitMapper;
import ru.practicum.ewm.server.model.Stats;
import ru.practicum.ewm.server.model.StatsMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public void createHit(EndpointHitDto endpointHitDto) {
        log.info("Сохранение статистики - {}", endpointHitDto);
        statRepository.save(HitMapper.toHit(endpointHitDto));
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Запрос статистики с - {} по - {} по uris - {}, уникальность - {}", start, end, uris, unique);
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
}
