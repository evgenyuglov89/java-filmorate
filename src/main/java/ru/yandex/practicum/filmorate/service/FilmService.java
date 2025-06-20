package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            log.warn("likeFilm не найден фильм с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID " + filmId + " не найден");
        }
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("likeFilm не найден пользователь с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден");
        }
        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Был поставлен лайк фильму с ID: {} пользователем с ID: {}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            log.warn("removeLike не найден фильм с ID: {}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID " + filmId + " не найден");
        }
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("removeLike не найден пользователь с ID: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Был удален лайк у фильма с ID: {} пользователем с ID: {}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Был возвращён список фильмов в кол-ве: {}", count);
        return filmStorage.filmsList().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
