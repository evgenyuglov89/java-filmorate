package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(
            @Qualifier("dbFilmStorage") FilmStorage filmStorage,
            @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.save(film);
    }

    public Film update(Film film) {
        int id = film.getId();

        Film existingFilm = filmStorage.findById(id);
        if (existingFilm == null) {
            log.warn("Попытка обновить несуществующий фильм с ID: {}", id);
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        return filmStorage.update(film);
    }

    public Film findById(int id) {
        return filmStorage.findById(id);
    }

    public List<Film> filmsList() {
        return filmStorage.filmsList();
    }

    public void likeFilm(int filmId, int userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            log.warn("likeFilm не найден фильм с ID: {}", filmId);
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("likeFilm не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");

        }
        filmStorage.likeFilm(filmId, userId);
        log.info("Был поставлен лайк фильму с ID: {} пользователем с ID: {}", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.findById(filmId);
        if (film == null) {
            log.warn("removeLike не найден фильм с ID: {}", filmId);
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        User user = userStorage.findById(userId);
        if (user == null) {
            log.warn("removeLike не найден пользователь с ID: {}", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        filmStorage.removeLike(filmId, userId);
        log.info("Был удален лайк у фильма с ID: {} пользователем с ID: {}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Был возвращён список фильмов в кол-ве: {}", count);
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getRecommendations(int userId) {
        return filmStorage.getRecommendations(userId);
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }
}
