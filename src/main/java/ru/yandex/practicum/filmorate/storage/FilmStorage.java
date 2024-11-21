package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film getFilmById(Integer id);

    Collection<Film> getPopularFilms(Integer count);

    Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year);

    Film create(Film film);

    Film update(Film film);

    void delete(Integer id);
}