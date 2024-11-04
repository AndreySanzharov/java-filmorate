package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeEach
    void beforeEach() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void afterEach() {
        validatorFactory.close();
    }

    @Test
    void createFilm() {
        Film film = Film.builder()
                .name("film")
                .description("des")
                .releaseDate(LocalDate.of(2024, 12, 12))
                .duration(5)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void createFailNameFilm() {
        Film film = Film.builder()
                .name("")
                .description("desc")
                .releaseDate(LocalDate.of(2024, 12, 12))
                .duration(5)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("name")));
    }

    @Test
    void createNullNameFilm() {
        Film film = Film.builder()
                .name(null)
                .description("desc")
                .releaseDate(LocalDate.of(2024, 12, 12))
                .duration(5)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("name")));
    }

    @Test
    void createNullDesc() {
        Film film = Film.builder()
                .name("film")
                .description(null)
                .releaseDate(LocalDate.of(2024, 12, 12))
                .duration(5)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
    }

    @Test
    void createEmptyDesc() {
        Film film = Film.builder()
                .name("film")
                .description(" ")
                .releaseDate(LocalDate.of(2024, 12, 12))
                .duration(5)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
    }

    @Test
    void createFilmWithInvalidReleaseDate() {
        Film film = Film.builder()
                .name("film")
                .description("desc")
                .releaseDate(LocalDate.of(1770, 1, 12))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("validReleaseDate")));
    }

    @Test
    void createNullReleaseDateFilm() {
        Film film = Film.builder()
                .name("film")
                .description("desc")
                .releaseDate(null) // Неверная дата релиза
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("releaseDate")));

    }

    @Test
    void createNegativeDurationFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Valid description")
                .releaseDate(LocalDate.of(2024, 12, 12))
                .duration(-1)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("duration")));
    }
}