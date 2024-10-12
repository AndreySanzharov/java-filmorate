package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Создание фильма");
        validateReleaseDate(film.getReleaseDate());

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан");
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обновление фильма");
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм обновлен");
            return oldFilm;
        }
        throw new NotFoundException("Пост с id " + newFilm.getId() + " не найден.");
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.info("Получение фильма по id");
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            throw new NotFoundException("Фильм не найден");
        }

    }

    private void validateReleaseDate(LocalDate releaseDate) {
        LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28); // Самая ранняя допустимая дата
        if (releaseDate.isBefore(earliestReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
    }

    private long getNextId() {
        Set<Long> allId = films.keySet();
        long maxId = 0;
        for (long id : allId) {
            if (id > maxId) {
                maxId = id;
            }
        }
        long currentID = ++maxId;
        return currentID;
    }
}
