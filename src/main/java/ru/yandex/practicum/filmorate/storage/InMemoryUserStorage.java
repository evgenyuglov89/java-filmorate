package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public User save(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        Long id = user.getId();

        if (!users.containsKey(id)) {
            log.warn("Попытка обновить несуществующего пользователя с ID: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        users.put(id, user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> usersList() {
        log.info("Получен список всех пользователей ({} шт.)", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }
}
