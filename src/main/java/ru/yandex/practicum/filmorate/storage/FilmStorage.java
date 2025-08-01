package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    List<Film> filmsList();

    Film findById(int id);

    void likeFilm(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getPopularFilms(int count);

    List<Film> getRecommendations(int userId);

    void delete(int id);

    List<Film> getFilmsByDirector(int directorId, String sortBy);
}
