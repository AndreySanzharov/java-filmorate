package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Integer filmId);

    Film getFilmById(Integer filmId);

    Collection<Film> getPopularFilms(Integer count);
}
