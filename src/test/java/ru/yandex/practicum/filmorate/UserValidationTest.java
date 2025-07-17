package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailIfEmailIsBlank() {
        User user = User.builder()
                .id(1)
                .login("login")
                .name("Name")
                .email("")
                .birthday(LocalDate.of(1992, 3, 9))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailIfEmailInvalid() {
        User user = User.builder()
                .id(1)
                .login("login")
                .name("Name")
                .email("invalid-email")
                .birthday(LocalDate.of(1992, 3, 9))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailIfLoginIsBlank() {
        User user = User.builder()
                .id(1)
                .login("")
                .name("Name")
                .email("test@mail.com")
                .birthday(LocalDate.of(1992, 3, 9))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailIfLoginHasSpaces() {
        User user = User.builder()
                .id(1)
                .login("user name")
                .name("Name")
                .email("test@mail.com")
                .birthday(LocalDate.of(1992, 3, 9))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailIfBirthdayInFuture() {
        User user = User.builder()
                .id(1)
                .login("login")
                .name("Name")
                .email("test@mail.com")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    void shouldPassIfNameIsBlank() {
        User user = User.builder()
                .id(1)
                .login("login")
                .name("")
                .email("test@mail.com")
                .birthday(LocalDate.of(1992, 3, 9))
                .build();
        assertEquals("login", user.getName());
    }

    @Test
    void shouldPassWithValidUser() {
        User user = User.builder()
                .id(1)
                .login("login")
                .name("Имя")
                .email("test@mail.com")
                .birthday(LocalDate.of(1992, 3, 9))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}