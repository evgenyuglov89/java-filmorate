package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventDbStorage {
    private final JdbcTemplate jdbc;
    private final EventRowMapper rowMapper;

    private static final String INSERT_EVENT = """
        INSERT INTO "events"
          ("user_id", "entity_id", "type", "operation")
        VALUES (?, ?, ?, ?)
    """;

    private static final String SELECT_FEED = """
        SELECT "id", "created_at", "user_id", "entity_id", "type", "operation"
        FROM "events"
        WHERE "user_id" = ?
        ORDER BY "created_at" ASC
    """;

    public void addEvent(Event event) {
        jdbc.update(
                INSERT_EVENT,
                event.getUserId(),
                event.getEntityId(),
                event.getEventType().name(),
                event.getOperation().name()
        );
    }

    public List<Event> getFeed(int userId) {
        return jdbc.query(SELECT_FEED, rowMapper, userId);
    }
}