package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FriendshipNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;

@Repository("dbUserStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;
    private static final String GET_ALL_USERS =
            "SELECT * FROM \"users\"";
    private static final String GET_USER_BY_ID =
            "SELECT * FROM \"users\" WHERE \"id\" = ?";
    private static final String GET_COUNT_USERS_BY_ID =
            "SELECT COUNT(*) FROM \"users\" WHERE \"id\" = ?";
    private static final String GET_COUNT_FRIENDS_BY_ID =
            "SELECT COUNT(*) FROM \"friendship\" WHERE \"user_id\" = ? AND \"friend_id\" = ?";
    private static final String GET_USER_FRIENDS =
            "SELECT \"friend_id\" FROM \"friendship\" AS \"g\" WHERE \"user_id\" = ?";
    private static final String GET_COMMON_FRIENDS = """
            SELECT u.* FROM "users" u
            JOIN "friendship" f1 ON u."id" = f1."friend_id"
            JOIN "friendship" f2 ON u."id" = f2."friend_id"
            WHERE f1."user_id" = ? AND f2."user_id" = ?
            """;
    private static final String INSERT_NEW_USER =
            "INSERT INTO \"users\" (\"name\", \"email\", \"login\", \"birthday\") VALUES (?, ?, ?, ?)";
    private static final String INSERT_NEW_FRIEND =
            "INSERT INTO \"friendship\" (\"user_id\", \"friend_id\", \"status\") VALUES (?, ?, ?)";
    private static final String UPDATE_USER =
            "UPDATE \"users\" SET \"name\" = ?, \"login\" = ?, \"birthday\" = ? WHERE \"id\" = ?";
    private static final String DELETE_FRIENDS =
            "DELETE FROM \"friendship\" WHERE \"user_id\" = ? AND \"friend_id\" = ?";

    @Override
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_NEW_USER, new String[]{"id"});
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getLogin());
            statement.setDate(4, Date.valueOf((user.getBirthday())));
            return statement;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    @Override
    public User update(User newUser) {
        Integer count = jdbc.queryForObject(GET_COUNT_USERS_BY_ID, new Object[]{newUser.getId()}, Integer.class);

        if (count != null && count > 0) {
            jdbc.update(UPDATE_USER,
                    newUser.getName(),
                    newUser.getLogin(),
                    newUser.getBirthday(),
                    newUser.getId());
        } else {
            throw new NotFoundException("Пользователя которого вы пытаетесь обновить не существует " + newUser.getId());
        }
        return newUser;
    }

    @Override
    public List<User> usersList() {
        return jdbc.query(GET_ALL_USERS, mapper);
    }

    @Override
    public User findById(int id) {
        return jdbc.query(GET_USER_BY_ID, (rs, rowNum) -> new UserRowMapper()
                        .mapRow(rs, rowNum), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        Integer count = jdbc.queryForObject(
                GET_COUNT_FRIENDS_BY_ID,
                new Object[]{userId, friendId},
                Integer.class
        );

        if (count != null && count > 0) {
            throw new IllegalStateException("Пользователи уже друзья");
        }

        jdbc.update(INSERT_NEW_FRIEND, userId, friendId, true);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        Integer count = jdbc.queryForObject(
                GET_COUNT_FRIENDS_BY_ID,
                new Object[]{userId, friendId},
                Integer.class
        );

        if (count == null || count == 0) {
            throw new FriendshipNotFoundException("Дружба между пользователями не существует");
        }

        jdbc.update(DELETE_FRIENDS, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        List<Integer> friendIds = jdbc.queryForList(GET_USER_FRIENDS, Integer.class, userId);

        if (friendIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(friendIds.size(), "?"));
        String query = String.format("SELECT * FROM \"users\" WHERE \"id\" IN (%s)", inSql);

        return jdbc.query(query, mapper, friendIds.toArray());
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        return jdbc.query(GET_COMMON_FRIENDS, mapper, userId, friendId);
    }
}
