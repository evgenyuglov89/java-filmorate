package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final List<User> users = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(currentId++);
        users.add(user);
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Optional<User> existingUser = users.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst();

        if (existingUser.isEmpty()) {
            log.warn("Попытка обновить несуществующего пользователя с ID: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        users.removeIf(u -> u.getId() == user.getId());
        users.add(user);
        log.info("Обновлён пользователь: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен список всех пользователей ({} шт.)", users.size());
        return users;
    }
}
