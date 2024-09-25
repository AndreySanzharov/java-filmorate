package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Cоздание фильма {}", film);
        validate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Ошибка валидации фильма: id должен быть указан.");
            throw new ValidationException("Id должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            validate(newFilm);


            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм успешно обновлен");
            return oldFilm;
        }
        throw new NotFoundException("Пост с id " + newFilm.getId() + " не найден.");
    }

    private void validate(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.error("Ошибка валидации фильма: пустое название");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка валидации фильма: превышен лимит символов в описании");
            throw new ValidationException("Максимальная длина описания - 200 символов. Фактичкеская длина: "
                    + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            log.error("Ошибка валидации фильма: неверная дата релиза");
            throw new ValidationException("Саммая ранняя дата релиза может бьть: 28.12.1895.");
        }
        if (film.getDuration().isNegative()) {
            log.error("Ошибка валидации фильма: продолжительность должна быть положительным числом");
            throw new ValidationException("Продолжительность должна быть положительным числом.");
        }
        if (film.getDescription().isBlank() || film.getDescription() == null) {
            log.error("Ошибка валидации фильма:  описание не может быть пустым");
            throw new ValidationException("Описание не может быть пустым.");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
