package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;
    private static final String GET_MPA = "SELECT * FROM \"mpa_rating\"";
    private static final String GET_MPA_BY_ID = "SELECT * FROM \"mpa_rating\" WHERE \"id\" = ?";

    public List<Mpa> mpaList() {
        return jdbcTemplate.query(GET_MPA, mpaRowMapper);
    }

    public Mpa findById(int id) {
        try {
            return jdbcTemplate.queryForObject(GET_MPA_BY_ID, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с id=" + id + " не найден");
        }
    }
}
