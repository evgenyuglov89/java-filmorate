package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User save(User user);

    User update(User user);

    List<User> usersList();

    User findById(int id);

    void addFriend(int userId, int friendsId);

    void deleteFriend(int userId, int friendsId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int otherId);

    void delete(int id);
}
