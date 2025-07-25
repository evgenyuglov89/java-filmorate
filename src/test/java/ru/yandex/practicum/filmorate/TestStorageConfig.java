package ru.yandex.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

@TestConfiguration
public class TestStorageConfig {

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }

    @Bean
    public FilmRowMapper filmRowMapper() {
        return new FilmRowMapper();
    }

    @Bean
    public GenreRowMapper genreRowMapper() {
        return new GenreRowMapper();
    }

    @Bean
    public MpaRowMapper mpaRowMapper() {
        return new MpaRowMapper();
    }

    @Bean
    public UserStorage userStorage(JdbcTemplate jdbcTemplate, UserRowMapper mapper) {
        return new UserDbStorage(jdbcTemplate, mapper);
    }

    @Bean
    public FilmStorage filmStorage(JdbcTemplate jdbcTemplate, FilmRowMapper mapper) {
        return new FilmDbStorage(jdbcTemplate, mapper);
    }

    @Bean
    public GenreDbStorage genreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper mapper) {
        return new GenreDbStorage(jdbcTemplate, mapper);
    }

    @Bean
    public MpaDbStorage mpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mapper) {
        return new MpaDbStorage(jdbcTemplate, mapper);
    }
}
