package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.save(user);
    }

    public User update(User user) {
        int id = user.getId();

        User existingFilm = userStorage.findById(id);
        if (existingFilm == null) {
            log.warn("Попытка обновить несуществующего пользователя с ID: {}", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        return userStorage.update(user);
    }

    public List<User> usersList() {
        return userStorage.usersList();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null) {
            log.warn("Не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        if (friend == null) {
            log.warn("Не найден пользователь с ID: {}", friendId);
            throw new NotFoundException("Пользователь с ID " + friendId + " не найден");
        }

        try {
            userStorage.addFriend(userId, friendId);
            log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
        } catch (IllegalStateException e) {
            log.warn("Дружба уже существует: userId={}, friendId={}", userId, friendId);
            throw e;
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (user == null) {
            log.warn("removeFriend не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        if (friend == null) {
            log.warn("removeFriend не найден пользователь с ID: {}", friendId);
            throw new NotFoundException("Пользователь с ID " + friendId + " не найден");
        }

        try {
            userStorage.deleteFriend(userId, friendId);
            log.debug("Удалена дружба между userId={} и friendId={}", userId, friendId);
        } catch (IllegalStateException e) {
            log.warn("Попытка удалить несуществующую дружбу: userId={}, friendId={}", userId, friendId);
            throw e;
        }
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("getFriends не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        List<User> friends = userStorage.getFriends(userId);
        log.info("Найдено {} друзей у пользователя с ID={}", friends.size(), userId);

        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);

        if (user == null || other == null) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        List<User> commonFriends = userStorage.getCommonFriends(userId, otherId);
        log.info("Был возвращён список {} общих друзей у пользователей с ID {} и {}",
                commonFriends.size(), userId, otherId);

        return commonFriends;
    }
}
