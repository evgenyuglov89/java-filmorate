package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    public ReviewService(
            ReviewDbStorage reviewDbStorage,
            @Qualifier("dbFilmStorage") FilmStorage filmStorage,
            @Qualifier("dbUserStorage") UserStorage userStorage, EventService eventService) {
        this.reviewDbStorage = reviewDbStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public Review create(Review review) {
        if (review.getFilmId() == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "filmId не передан");
        }
        if (review.getUserId() == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "userId не передан");
        }
        Film existingFilm = filmStorage.findById(review.getFilmId());
        if (existingFilm == null) {
            throw new NotFoundException("Фильм с ID " + review.getFilmId() + " не найден");
        }
        User existingUser = userStorage.findById(review.getUserId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с ID " + review.getUserId() + " не найден");
        }

        Review saved = reviewDbStorage.save(review);
        eventService.logEvent(saved.getUserId(), saved.getReviewId(), EventType.REVIEW, Operation.ADD);

        return saved;
    }

    public Review update(Review review) {
        int id = review.getReviewId();

        Review existingReview = reviewDbStorage.findById(id);
        if (existingReview == null) {
            log.warn("Попытка обновить несуществующий отзыв с ID: {}", id);
            throw new NotFoundException("Отзыв с ID " + id + " не найден");
        }

        eventService.logEvent(review.getUserId(), id, EventType.REVIEW, Operation.UPDATE);
        return reviewDbStorage.update(review);
    }

    public void delete(int id) {
        Review review = reviewDbStorage.findById(id);
        eventService.logEvent(review.getUserId(), id, EventType.REVIEW, Operation.REMOVE);
        reviewDbStorage.delete(id);
    }

    public Review findById(int id) {
        return reviewDbStorage.findById(id);
    }

    public List<Review> getReviews(Integer filmId, int count) {
        if (filmId != null) {
            return reviewDbStorage.findByFilmIdOrderByUsefulDesc(filmId, count);
        } else {
            return reviewDbStorage.findAllOrderByUsefulDesc(count);
        }
    }

    public void createReaction(int reviewId, int userId, boolean isLike) {
        reviewDbStorage.createReaction(reviewId, userId, isLike);
    }

    public void deleteReaction(int reviewId, int userId, boolean isLike) {
        reviewDbStorage.deleteReaction(reviewId, userId, isLike);
    }
}
