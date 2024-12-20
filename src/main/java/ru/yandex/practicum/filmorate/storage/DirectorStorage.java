package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DirectorStorage {
    List<Director> getList();

    Director getById(int id);

    Director create(Director director);

    Director update(Director director);

    void delete(int id);

    Collection<Director> getDirectorByFilm(int filmId);

    void deleteFilmDirector(int filmId);

    void createFilmDirector(int filmId, int directorId);

    Map<Integer, List<Director>> getDirectorsByFilmIds(List<Integer> filmIds);
}
