package ru.practicum.ewm.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Stats {
    private String app;
    private String uri;
    private Long hits;
}
