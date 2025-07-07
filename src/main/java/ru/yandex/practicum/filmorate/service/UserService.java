package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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
        if (user == null) {
            log.warn("addFriend не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User friend = userStorage.findById(friendId);
        if (friend == null) {
            log.warn("addFriend не найден пользователь с ID: {}", friendId);
            throw new NotFoundException("Пользователь с ID " + friendId + " не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Был добавлен друг с ID: {} пользователю с ID: {}", friendId, userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("removeFriend не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User friend = userStorage.findById(friendId);
        if (friend == null) {
            log.warn("removeFriend не найден пользователь с ID: {}", friendId);
            throw new NotFoundException("Пользователь с ID " + friendId + " не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Был удален друг с ID: {} у пользователя с ID: {}", friendId, userId);
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("getFriends не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        log.info("Был возвращён список друзей у пользователя с ID: {}", userId);
        return user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = userStorage.findById(userId).getFriends();
        Set<Integer> otherFriends = userStorage.findById(otherId).getFriends();
        log.info("Был возвращён список общих друзей у пользователя с ID: {} и пользователя с ID: {}", userId, otherId);
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
