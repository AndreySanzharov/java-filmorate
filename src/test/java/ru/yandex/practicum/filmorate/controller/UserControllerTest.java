package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
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
    void createUser() {
        User user = User.builder()
                .name("user")
                .email("user@mail.com")
                .login("login")
                .birthday(LocalDate.of(2004, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    void createEmptyEmailUser() {
        User user = User.builder()
                .name("user")
                .email(" ")
                .login("login")
                .birthday(LocalDate.of(2004, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }

    @Test
    void createNullEmailUser() {
        User user = User.builder()
                .name("user")
                .email(null)
                .login("login")
                .birthday(LocalDate.of(2004, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }

    @Test
    void createEmailWithoutAt() {
        User user = User.builder()
                .name("user")
                .email("usermail.com")
                .login("login")
                .birthday(LocalDate.of(2004, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
    }


    @Test
    void createEmptyLoginUser() {
        User user = User.builder()
                .name("user")
                .email("user@mail.com")
                .login(" ")
                .birthday(LocalDate.of(2004, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("login")));
    }

    @Test
    void createFailLoginUser() {
        User user = User.builder()
                .name("user")
                .email("user@mail.com")
                .login("lo gin")
                .birthday(LocalDate.of(2004, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("login")));
    }

    @Test
    void createFailBirthdayUser() {
        User user = User.builder()
                .name("user")
                .email("user@mail.com")
                .login("login")
                .birthday(LocalDate.of(2030, 12, 12))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream()
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("birthday")));
    }
}





