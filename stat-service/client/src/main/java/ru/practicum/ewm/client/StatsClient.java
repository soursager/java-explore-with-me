package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    private static final String STATS_API_PREFIX = "/stats?start={start}&end={end}&unique={unique}";

    @Autowired
    public StatsClient(@Value("${server.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void createHit(String app, String uri, String ip, String timestamp) {
        EndpointHitDto hit = new EndpointHitDto(app, uri, ip, LocalDateTime.parse(timestamp,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        post("/hit", hit);
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>(Map.of("start", encode(start),
                "end", encode(end),
                "unique", unique));

        if (uris != null) {

            parameters.put("uris", String.join(",", uris));
            return get(STATS_API_PREFIX + "&uris={uris}", parameters);
        }
        return get(STATS_API_PREFIX, parameters);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}