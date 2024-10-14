package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private long currentID = 0;

    private final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void like(Long filmId, Long userId) {
        log.info("Запрос на лайк");
        if (getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        if (userStorage.getUserById(Math.toIntExact(userId)) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        getFilmById(filmId).getLikes().add(userId);
        log.info("Лайк поставлен");
    }

    public Film create(Film film) {
        log.info("Создание фильма");
        validateReleaseDate(film.getReleaseDate());

        film.setId(getNextId());
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Запрос на удаление лайка");
        if (getFilmById(filmId).getLikes().remove(userId)) {
            getFilmById(filmId).getLikes().remove(userId);
            log.info("Лайк удален");
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение топа фильмов");
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        // Самая ранняя допустимая дата
        if (releaseDate.isBefore(earliestReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }


    private long getNextId() {
        return ++currentID;
    }
}
