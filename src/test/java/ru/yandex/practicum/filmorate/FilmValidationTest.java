package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailIfNameBlank() {
        Film film = new Film(1, "", "desc",
                LocalDate.of(2000, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFailIfDescriptionTooLong() {
        String longDescription = "a".repeat(201);
        Film film = new Film(1, "Film", longDescription,
                LocalDate.of(2000, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailIfReleaseDateBefore1895() {
        Film film = new Film(1, "Film", "desc",
                LocalDate.of(1895, 12, 27), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFailIfDurationNotPositive() {
        Film film = new Film(1, "Film", "desc",
                LocalDate.of(2000, 1, 1), 0);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldPassWithValidFilm() {
        Film film = new Film(1, "Film", "desc",
                LocalDate.of(2000, 1, 1), 100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}