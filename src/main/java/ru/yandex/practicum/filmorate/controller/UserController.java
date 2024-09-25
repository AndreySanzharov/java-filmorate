package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        validate(user);

        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;

    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
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
            }

            return oldUser;
        }
        throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден.");
    }

    private void validate(User user) {
        if (user.getEmail().isBlank() || user.getEmail() == null) {
            throw new ValidationException("Почта не должна быть пустой.");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Почта должна содержать @.");
        }
        if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не должен быть пустым.");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
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
//    создание пользователя;
//    обновление пользователя;
//    получение списка всех пользователей.
}
