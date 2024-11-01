package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Integer filmId);

    Optional<Film> getFilmById(Long filmId);

    Collection<Film> getPopularFilms(Integer count);
}
