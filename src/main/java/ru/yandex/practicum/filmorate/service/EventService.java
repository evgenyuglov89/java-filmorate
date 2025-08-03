package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventDbStorage eventDbStorage;
    private final UserStorage userDbStorage;

    public void logEvent(int userId, long entityId, EventType type, Operation op) {
        if (userDbStorage.findById(userId) == null) {
            throw new NotFoundException("User not found: " + userId);
        }
        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(type)
                .operation(op)
                .build();
        eventDbStorage.addEvent(event);
    }

    public List<Event> getFeed(int userId) {
        if (userDbStorage.findById(userId) == null) {
            throw new NotFoundException("User not found: " + userId);
        }
        return eventDbStorage.getFeed(userId);
    }
}