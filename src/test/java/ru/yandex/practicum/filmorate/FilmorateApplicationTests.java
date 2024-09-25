package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    private final UserController userController = new UserController();

    @Test
    void WhenEmailIsBlank() {
        User user = new User();
        user.setEmail("");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void WhenEmailDoesNotContainAtSymbol() {
        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void WhenLoginIsBlank() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void WhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("invalid login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void WhenBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.now().plusDays(1)); // Future date

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldUseLoginAsNameIfNameIsBlank() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assert createdUser.getName().equals("validLogin");
    }

}
