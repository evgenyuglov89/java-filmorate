package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper;
    private static final String GET_DIRECTORS = "SELECT * FROM \"directors\"";
    private static final String GET_DIRECTOR_BY_ID = "SELECT * FROM \"directors\" WHERE \"id\" = ?";
    private static final String INSERT_NEW_DIRECTOR = "INSERT INTO \"directors\" (\"name\") VALUES (?)";
    private static final String UPDATE_DIRECTOR = "UPDATE \"directors\" SET \"name\" = ? WHERE \"id\" = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM \"directors\" WHERE \"id\" = ?";

    public List<Director> direcrorsList() {
        return jdbcTemplate.query(GET_DIRECTORS, directorRowMapper);
    }

    public Director findById(int id) {
        try {
            return jdbcTemplate.queryForObject(GET_DIRECTOR_BY_ID, directorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Director с id=" + id + " не найден");
        }
    }

    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_NEW_DIRECTOR, Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            director.setId(key.intValue());
        }
        return director;
    }

    public Director updateDirector(Director director) {
        int rowsUpdated = jdbcTemplate.update(UPDATE_DIRECTOR, director.getName(), director.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Director c id=" + director.getId() + " не существует.");
        }
        return director;
    }

    public void removeDirector(int id) {
        int rowsDeleted = jdbcTemplate.update(DELETE_DIRECTOR, id);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Director c id=" + id + " не существует.");
        }
    }
}