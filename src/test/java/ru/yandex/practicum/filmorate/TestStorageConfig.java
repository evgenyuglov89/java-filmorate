package ru.yandex.practicum.filmorate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.mappers.*;

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
    public DirectorRowMapper directorRowMapper() {
        return new DirectorRowMapper();
    }

    @Bean
    public UserStorage userStorage(JdbcTemplate jdbcTemplate, UserRowMapper mapper) {
        return new UserDbStorage(jdbcTemplate, mapper);
    }

    @Bean
    public FilmStorage filmStorage(JdbcTemplate jdbcTemplate,
                                   FilmRowMapper filmMapper,
                                   DirectorRowMapper directorMapper) {
        return new FilmDbStorage(jdbcTemplate, filmMapper, directorMapper);
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
