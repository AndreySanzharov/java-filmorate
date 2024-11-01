package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String ALL_GENRES_QUERY = "SELECT * FROM GENRES";
    private static final String INSERT_QUERY = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String GENRE_BY_ID_QUERY = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
    private static final String DELETE_ALL_FROM_FILM_QUERY = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getAllGenres() {
        return findMany(ALL_GENRES_QUERY);
    }

    public Optional<User> getGenreById(Integer id) {
        return findOne(GENRE_BY_ID_QUERY, id);
    }

    public void addGenres(Integer filmId, List<Integer> genresIds) {
        update(INSERT_QUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresIds.size();
            }
        });
    }

    public void deleteGenres(Integer filmId) {
        update(DELETE_ALL_FROM_FILM_QUERY, filmId);
    }
}