package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FOR_ALL_GENRES_QUERY = "SELECT * FROM GENRES ORDER BY genre_id";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String FOR_GENRE_BY_ID_QUERY = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String DELETE_ALL_FROM_FILM_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> findAllGenres() {
        return findMany(FOR_ALL_GENRES_QUERY);
    }

    public Genre getGenreById(Integer id) {
        return findOne(FOR_GENRE_BY_ID_QUERY, id);
    }

    public void addGenres(Integer filmId, List<Integer> genreIds) {
        jdbc.batchUpdate(INSERT_GENRES_QUERY, genreIds, genreIds.size(), (ps, genreId) -> {
            ps.setInt(1, filmId);
            ps.setInt(2, genreId);
        });
    }

    public void deleteGenres(Integer filmId) {
        update(DELETE_ALL_FROM_FILM_QUERY, filmId);
    }
}