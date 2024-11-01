package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository implements FilmStorage {
    private final String INSERT_QUERY = "INSERT INTO FILMS " +
            "(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, " +
            "RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private final String GET_FILM_BY_ID_QUERY = "SELECT * FROM FILMS f, MPA_RATINGS m " +
            "WHERE f.MPA_ID = m.MPA_ID AND f.FILM_ID = ?";
    private final String DELETE_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private final String FIND_ALL_FILMS_QUERY = "SELECT * FROM FILMS f, " +
            "MPA_RATINGS m WHERE f.MPA_ID = m.MPA_ID";
    private final String TOP_FILMS_QUERY = "SELECT * FROM FILMS f LEFT JOIN MPA_RATINGS m " +
            "ON f.MPA_ID = m.MPA_ID LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES " +
            "GROUP BY FILM_ID) fl ON f.FILM_ID = fl.FILM_ID ORDER BY LIKES DESC LIMIT ?";


    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film create(Film film) {
        Integer id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public void delete(Integer filmId) {
        delete(DELETE_QUERY, filmId);
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_FILMS_QUERY);
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        Optional film = findOne(GET_FILM_BY_ID_QUERY, filmId);
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return findMany(TOP_FILMS_QUERY, count);
    }
}
