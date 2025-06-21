package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @Override
    public Film save(Film film) {
        film.setId(currentId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    public List<Film> filmsList() {
        log.info("Получен список всех фильмов ({} шт.)", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(int id) {
        return films.get(id);
    }
}
