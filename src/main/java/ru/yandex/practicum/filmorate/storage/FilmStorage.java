package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film getFilmById(Integer id);

    Collection<Film> getPopularFilms(Integer count);

    Film create(Film film);

    Film update(Film film);

    void delete(Integer id);
}