package ru.practicum.ewm.server.model;

import ru.practicum.ewm.dto.ViewStatsDto;

public class StatsMapper {
    public static ViewStatsDto toViewStatsDto(Stats stats) {
        return ViewStatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .hits(stats.getHits())
                .build();
    }
}
