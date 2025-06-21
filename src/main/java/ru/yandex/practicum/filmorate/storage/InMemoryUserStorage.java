package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User save(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @Override
    public List<User> usersList() {
        log.info("Получен список всех пользователей ({} шт.)", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(int id) {
        return users.get(id);
    }
}
