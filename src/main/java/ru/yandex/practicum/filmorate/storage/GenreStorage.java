package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {
    Collection<Genre> findAllGenres();

    Genre getGenreById(Integer id);

    void addGenres(Integer filmId, List<Integer> genreIds);

    void deleteGenres(Integer filmId);
}
