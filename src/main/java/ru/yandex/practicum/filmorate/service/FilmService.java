package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void like(Long filmId, Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmStorage.getFilmById(filmId));
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        log.info("Лайк поставлен");
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Пользователь {} удаляет лайк с фильма {}", userId, filmStorage.getFilmById(filmId));
        if (filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            filmStorage.getFilmById(filmId).getLikes().remove(userId);
            log.info("Лайк удален");
        } else {
            throw new NotFoundException("Пользователь не найден или не ставил лайк фильму");
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Вывод топ {} фильмов", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


}
