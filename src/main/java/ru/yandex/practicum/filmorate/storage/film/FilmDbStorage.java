package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmMapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rate) VALUES (?, ?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ? WHERE id = ?";

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, filmMapper);
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setString(5, film.getRating());
            return ps;
        }, keyHolder);
        film.setId(Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElseThrow(() -> new NotFoundException("Failed to create film")));
        return film;
    }

    @Override
    public Film update(Film film) {
        int rowsUpdated = jdbcTemplate.update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRating(), film.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Film not found for update");
        }
        return film;
    }


    @Override
    public Optional<Film> getFilmById(Long filmId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, filmMapper, filmId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
