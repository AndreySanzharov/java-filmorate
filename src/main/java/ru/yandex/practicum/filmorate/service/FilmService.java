package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;
    private final JdbcTemplate jdbc;

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        likesRepository.addLike(filmId, userId);
        return film;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        likesRepository.deleteLike(filmId, userId);
        return film;
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        validateMpaExists(film.getMpa().getId());
        validateGenresExist(film.getGenres());
        Film createdFilm = filmStorage.create(film);
        if (!createdFilm.getGenres().isEmpty()) {
            genreRepository.addGenres(createdFilm.getId(), createdFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        return createdFilm;
    }

    public Film update(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Неверный идентификатор фильма");
        }
        Film updatedFilm = filmStorage.update(film);
        if (!updatedFilm.getGenres().isEmpty()) {
            genreRepository.deleteGenres(updatedFilm.getId());
            genreRepository.addGenres(updatedFilm.getId(), updatedFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        return updatedFilm;
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }

    private void validateMpaExists(Integer mpaId) {
        String checkMpaQuery = "SELECT COUNT(*) FROM MPA_RATINGS WHERE MPA_ID = ?";
        Integer count = jdbc.queryForObject(checkMpaQuery, Integer.class, mpaId);
        if (count == null || count == 0) {
            log.error("MPA с id = {} не существует", mpaId);
            throw new ValidationException("MPA с id = " + mpaId + " не существует.");
        }
    }


    private void validateGenresExist(Set<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            String genreIds = genres.stream()
                    .map(genre -> String.valueOf(genre.getId()))
                    .collect(Collectors.joining(", "));

            String query = String.format("SELECT COUNT(*) FROM GENRES WHERE GENRE_ID IN (%s)", genreIds);
            Integer existingCount = jdbc.queryForObject(query, Integer.class);

            if (existingCount == null || existingCount < genres.size()) {
                log.error("Один или несколько жанров не существуют: {}", genreIds);
                throw new ValidationException("Некоторые жанры не существуют в базе данных.");
            }
        }
    }
}