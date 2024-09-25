package ru.yandex.practicum.filmorate.controller;

import org.springframework.core.annotation.MergedAnnotationPredicates;
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

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов. Фактичкеская длина: "
                    + film.getDescription().length());
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1985, 12, 28))) {
            throw new ValidationException("Саммая рання дата релиза может бьть: 28.12.1895.");
        } else if (film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность должна быть положительным числом.");
        } else {
            film.setId(getNextId());
            films.put(film.getId(), film);
            return film;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getDescription() == null || newFilm.getDescription().isBlank()) {
                throw new ValidationException("Описание не может быть пустым");
            }
            oldFilm.setDescription(newFilm.getDescription());
            return oldFilm;
        }
        throw new NotFoundException("Пост с id " + newFilm.getId() + " не найден");
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    //добавление фильма;
    //обновление фильма;
    //получение всех фильмов.


}
