package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryUserStorage")
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

    @Override
    public void addFriend(int userId, int friendId) {
        User user = findById(userId);
        user.getFriends().add(friendId);
        update(user);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = findById(userId);
        user.getFriends().remove(friendId);
        update(user);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = findById(userId);

        return user.getFriends().stream()
                .map(this::findById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = findById(userId).getFriends();
        Set<Integer> otherFriends = findById(otherId).getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::findById)
                .collect(Collectors.toList());
    }
}
