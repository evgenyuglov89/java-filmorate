package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .timestamp(rs.getTimestamp("created_at").toInstant().toEpochMilli())
                .userId(rs.getInt("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .build();
    }
}