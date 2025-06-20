package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User save(User user);

    User update(User user);

    List<User> usersList();

    User findById(Long id);
}
