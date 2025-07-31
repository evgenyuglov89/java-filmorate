package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;

    private static final String GET_COUNT_REVIEWS_BY_ID = """
            SELECT COUNT(*) FROM "reviews" WHERE "id" = ?""";
    private static final String GET_REVIEWS_BY_ID = """
            SELECT r."id", r."film_id", r."user_id", r."content", r."is_positive",
            COALESCE(SUM(rr."useful"), 0) AS "useful"
            FROM "reviews" r
            LEFT JOIN "review_reactions" rr ON r."id" = rr."review_id"
            WHERE r."id" = ?
            GROUP BY r."id", r."film_id", r."user_id", r."content", r."is_positive"
            """;
    private static final String GET_REVIEWS_BY_FILM_ID = """
            SELECT r."id", r."film_id", r."user_id", r."content", r."is_positive",
            COALESCE(SUM(rr."useful"), 0) AS "useful"
            FROM "reviews" r
            LEFT JOIN "review_reactions" rr ON r."id" = rr."review_id"
            WHERE r."film_id" = ?
            GROUP BY r."id", r."film_id", r."user_id", r."content", r."is_positive"
            ORDER BY "useful" DESC
            LIMIT ?""";
    private static final String GET_ALL_REVIEWS = """
            SELECT r."id", r."film_id", r."user_id", r."content", r."is_positive",
            COALESCE(SUM(rr."useful"), 0) AS "useful"
            FROM "reviews" r
            LEFT JOIN "review_reactions" rr ON r."id" = rr."review_id"
            GROUP BY r."id", r."film_id", r."user_id", r."content", r."is_positive"
            ORDER BY "useful" DESC
            LIMIT ?""";
    private static final String INSERT_NEW_REVIEW = """
            INSERT INTO "reviews" ("film_id", "user_id", "content", "is_positive") VALUES (?, ?, ?, ?)""";
    private static final String INSERT_NEW_REVIEW_REACTIONS = """
            INSERT INTO "review_reactions" ("review_id", "user_id", "useful") VALUES (?, ?, ?)""";
    private static final String UPDATE_REVIEW = """
            UPDATE "reviews" SET "film_id" = ?, "user_id" = ?,"content" = ?, "is_positive" = ? WHERE "id" = ?""";
    private static final String DELETE_REVIEW_BY_ID = """
            DELETE FROM "reviews" WHERE "id" = ?""";
    private static final String DELETE_REVIEW_REACTION = """
            DELETE FROM "review_reactions" WHERE "review_id" = ? AND "user_id" = ? AND "useful" = ?""";

    public Review save(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_NEW_REVIEW,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, review.getFilmId());
            statement.setInt(2, review.getUserId());
            statement.setString(3, review.getContent());
            statement.setBoolean(4, review.getIsPositive());
            return statement;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys == null || !keys.containsKey("id")) {
            throw new RuntimeException("Не удалось получить ID нового отзыва");
        }
        int reviewId = ((Number) keys.get("id")).intValue();
        review.setReviewId(reviewId);

        return review;
    }

    public Review findById(int id) {
        return jdbc.query(GET_REVIEWS_BY_ID, mapper, id).stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Отзыв с ID " + id + " не найден"));
    }

    public Review update(Review review) {
        Integer count = jdbc.queryForObject(GET_COUNT_REVIEWS_BY_ID, new Object[]{review.getReviewId()}, Integer.class);

        if (count != null && count > 0) {
            jdbc.update(UPDATE_REVIEW,
                    review.getFilmId(),
                    review.getUserId(),
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());
        } else {
            throw new NotFoundException("Отзыв который вы пытаетесь обновить не существует " + review.getReviewId());
        }
        return review;
    }

    public void delete(int id) {
        jdbc.update(DELETE_REVIEW_BY_ID, id);
    }

    public List<Review> findByFilmIdOrderByUsefulDesc(int filmId, int count) {
        return jdbc.query(GET_REVIEWS_BY_FILM_ID, mapper, filmId, count);
    }

    public List<Review> findAllOrderByUsefulDesc(int count) {
        return jdbc.query(GET_ALL_REVIEWS, mapper, count);
    }

    public void createReaction(int reviewId, int userId, boolean isPositive) {
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT_NEW_REVIEW_REACTIONS);
            statement.setInt(1, reviewId);
            statement.setInt(2, userId);
            statement.setInt(3, isPositive ? 1 : -1);
            return statement;
        });
    }

    public void deleteReaction(int reviewId, int userId, boolean isPositive) {
        jdbc.update(DELETE_REVIEW_REACTION, reviewId, userId, isPositive ? 1 : -1);
    }
}
