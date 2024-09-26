package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Cоздание пользователя {}", user);
        validate(user);

        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь успешно создан");

        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка валидации пользователя. Id не указан. {}", newUser);
            throw new ValidationException("Id должен быть указан.");
        }
        if (users.containsKey(newUser.getId())) {
            validate(newUser);
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            if (newUser.getName().isBlank() || newUser.getName() == null) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }

            log.info("Пользователь успешно обновлен {}", oldUser);

            return oldUser;
        }
        throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден.");
    }

    private void validate(User user) {
        if (user.getEmail().isBlank() || user.getEmail() == null) {
            log.error("Ошибка валидации пользователя: пустая почта");
            throw new ValidationException("Почта не должна быть пустой.");
        } else if (!user.getEmail().contains("@")) {
            log.error("Ошибка валидации пользователя: отсутствие символа @ в почте");
            throw new ValidationException("Почта должна содержать @.");
        }
        if (user.getLogin().isBlank()) {
            log.error("Ошибка валидации пользователя: пустой логин");
            throw new ValidationException("Логин не должен быть пустым.");
        } else if (user.getLogin().contains(" ")) {
            log.error("Ошибка валидации пользователя: логин содержит пробелы");
            throw new ValidationException("Логин не должен содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации пользователя: неверно указана дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
