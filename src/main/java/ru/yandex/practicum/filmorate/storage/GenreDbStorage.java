package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;
    private static final String GET_GENRES = "SELECT * FROM \"genres\"";
    private static final String GET_GENRE_BY_ID = "SELECT * FROM \"genres\" WHERE \"id\" = ?";

    public List<Genre> genresList() {
        return jdbcTemplate.query(GET_GENRES, genreRowMapper);
    }

    public Genre findById(int id) {
        try {
            return jdbcTemplate.queryForObject(GET_GENRE_BY_ID, genreRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Genre с id=" + id + " не найден");
        }
    }
}
