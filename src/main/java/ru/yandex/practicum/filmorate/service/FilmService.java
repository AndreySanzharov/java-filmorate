package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;
    private final DirectorStorage directorStorage;
    private final JdbcTemplate jdbc;
    private final FeedRepository feedService;
    private final UserService userService;

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);

        if (likesRepository.getLikesByFilmAndUserId(filmId, userId).isEmpty()) {
            likesRepository.addLike(filmId, userId);
        }

        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType("LIKE")
                .operation("ADD")
                .entityId(filmId)
                .build());
        return film;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        film.getLikes().remove(userId);
        likesRepository.deleteLike(filmId, userId);
        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType("LIKE")
                .operation("REMOVE")
                .entityId(filmId)
                .build());
        return film;
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();

        films.forEach(film -> {
            Collection<Director> directors = directorStorage.getDirectorByFilm(film.getId());
            film.setDirectors((List<Director>) directors);
        });

        return films;
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);

        try {
            List<Director> director = (List<Director>) directorStorage.getDirectorByFilm(id);
            film.setDirectors(director);
        } catch (NotFoundException e) {
            log.error("Director с filmId = {} не существует", id);
        }

        return film;
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

        if (!createdFilm.getDirectors().isEmpty()) {
            int directorId = createdFilm.getDirectors().getFirst().getId();
            directorStorage.createFilmDirector(createdFilm.getId(), directorId);
        }

        return createdFilm;
    }

    public Film update(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Неверный идентификатор фильма");
        }
        Film updatedFilm = filmStorage.update(film);

        genreRepository.deleteGenres(updatedFilm.getId());
        if (!film.getGenres().isEmpty()) {
            genreRepository.addGenres(updatedFilm.getId(), film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }

        directorStorage.deleteFilmDirector(updatedFilm.getId());
        if (!film.getDirectors().isEmpty()) {
            int directorId = film.getDirectors().getFirst().getId();
            directorStorage.createFilmDirector(updatedFilm.getId(), directorId);
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

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        Collection<Film> commonFilms = filmStorage.getCommonFilms(userId, friendId);

        if (commonFilms.isEmpty()) {
            log.info("Общие фильмы между пользователями {} и {} не найдены.", userId, friendId);
        }

        return commonFilms;
    }

    public Collection<Film> getFilmsByDirector(int directorId, String sortBy) {
        Collection<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);

        if (films.isEmpty()) {
            log.info("Фильмы режиссера с id = {} не найдены.", directorId);
            throw new NotFoundException("Фильмы не найдены");
        }

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Integer, List<Director>> directorsByFilmId = directorStorage.getDirectorsByFilmIds(filmIds);

        films.forEach(film -> film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), List.of())));

        return films;
    }

//    public Collection<Film> search(String query, String by) {
//        Collection<Film> films = filmStorage.search(query, by);
//
//        films.forEach(film -> {
//            Collection<Director> directors = directorStorage.getDirectorByFilm(film.getId());
//            film.setDirectors((List<Director>) directors);
//        });
//
//        return films;
//    }

    public Collection<Film> search(String query, String by) {
        // Получаем все фильмы, соответствующие критериям поиска
        Collection<Film> films = filmStorage.search(query, by);

        // Получаем всех директоров для найденных фильмов за один запрос
        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Integer, List<Director>> directorsMap = directorStorage.getDirectorsByFilmIds(filmIds);

        // Устанавливаем директоров для каждого фильма
        films.forEach(film ->
                film.setDirectors(directorsMap.getOrDefault(film.getId(), new ArrayList<>())));

        return films;
    }

    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        Collection<Film> films = filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);

        films.forEach(film -> {
            Collection<Director> directors = directorStorage.getDirectorByFilm(film.getId());
            film.setDirectors((List<Director>) directors);
        });

        return films;
    }
}