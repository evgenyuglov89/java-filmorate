package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Long, Film> films = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public Film save(Film film) {
        film.setId(currentId++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Long id = film.getId();

        if (!films.containsKey(id)) {
            log.warn("Попытка обновить несуществующий фильм с ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }

        films.put(id, film); // Обновление по ключу
        log.info("Обновлён фильм: {}", film);
        return film;
    }

    @Override
    public List<Film> filmsList() {
        log.info("Получен список всех фильмов ({} шт.)", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }
}
