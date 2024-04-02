package ru.practicum.ewm.server.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.server.model.Hit;
import ru.practicum.ewm.server.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {

    @Query(value = "SELECT new ru.practicum.ewm.server.model.Stats(app, uri, COUNT(DISTINCT ip))" +
            "FROM Hit " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY 3 DESC")
    List<Stats> findAllUniqueHitsByUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.ewm.server.model.Stats(app, uri, COUNT(DISTINCT ip))" +
            "FROM Hit " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri " +
            "ORDER BY 3 DESC")
    List<Stats> findAllUniqueHits(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.ewm.server.model.Stats(app, uri, COUNT(ip))" +
            "FROM Hit " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri " +
            "ORDER BY 3 DESC")
    List<Stats> findAllHits(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.ewm.server.model.Stats(app, uri, COUNT(ip))" +
            "FROM Hit " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY 3 DESC")
    List<Stats> findAllHitsByUri(LocalDateTime start, LocalDateTime end, List<String> uris);
}
