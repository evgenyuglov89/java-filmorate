package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(180)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void shouldFailIfDescriptionTooLong() {
        String longDescription = "a".repeat(201);
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description(longDescription)
                .releaseDate(LocalDate.of(2000, 1, 11))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void shouldFailIfReleaseDateBefore1895() {
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("desc")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    void shouldFailIfDurationNotPositive() {
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 1, 11))
                .duration(0)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    void shouldPassWithValidFilm() {
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("desc")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(new Mpa(1, null, null))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}