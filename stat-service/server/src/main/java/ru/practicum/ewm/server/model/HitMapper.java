package ru.practicum.ewm.server.model;

import ru.practicum.ewm.dto.EndpointHitDto;

public class HitMapper {
    public static Hit toHit(EndpointHitDto endpointHitDto) {
        return Hit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
