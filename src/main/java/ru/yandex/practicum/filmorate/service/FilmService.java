package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }


    public void like(Long filmId, Long userId) {
        log.info("Запрос на лайк");
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        if (userStorage.getUserById(Math.toIntExact(userId)) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        log.info("Лайк поставлен");
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Запрос на удаление лайка");
        if (filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            filmStorage.getFilmById(filmId).getLikes().remove(userId);
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


}
