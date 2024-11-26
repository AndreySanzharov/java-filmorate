package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final String FOR_ALL_FILMS_QUERY = "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
            "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME " +
            "FROM FILMS f " +
            "JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID";
    private static final String FOR_FILM_BY_ID_QUERY =
            "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
                    "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME " +
                    "FROM FILMS f " +
                    "INNER JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
                    "WHERE f.FILM_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, " +
            "DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, " +
            "RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";
    private static final String TOP_FILMS_QUERY = "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
            "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME, COALESCE(fl.LIKES, 0) AS LIKES " +
            "FROM FILMS f " +
            "LEFT JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
            "LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES GROUP BY FILM_ID) fl " +
            "ON f.FILM_ID = fl.FILM_ID " +
            "ORDER BY LIKES DESC LIMIT ?";
    private static final String ALL_GENRES_FILMS_QUERY = "SELECT fg.FILM_ID, g.GENRE_ID, g.GENRE_NAME " +
            "FROM FILMS_GENRES fg " +
            "JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID";
    private static final String GENRES_BY_FILM_QUERY = "SELECT g.GENRE_ID, g.GENRE_NAME " +
            "FROM GENRES g " +
            "JOIN FILMS_GENRES fg ON g.GENRE_ID = fg.GENRE_ID " +
            "WHERE fg.FILM_ID = ?";

    private static final String COMMON_FILMS_QUERY =
            "SELECT f.*, m.MPA_NAME, COUNT(fl.USER_ID) AS LIKES " +
                    "FROM FILMS f " +
                    "JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
                    "JOIN FILMS_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                    "WHERE f.FILM_ID IN ( " +
                    "    SELECT fl1.FILM_ID " +
                    "    FROM FILMS_LIKES fl1 " +
                    "    JOIN FILMS_LIKES fl2 ON fl1.FILM_ID = fl2.FILM_ID " +
                    "    WHERE fl1.USER_ID = ? AND fl2.USER_ID = ? " +
                    ") " +
                    "GROUP BY f.FILM_ID, m.MPA_NAME " +
                    "ORDER BY LIKES DESC";

    private static final String GENRES_BY_FILM_IDS_QUERY =
            "SELECT fg.FILM_ID, g.GENRE_ID, g.GENRE_NAME " +
                    "FROM FILMS_GENRES fg " +
                    "JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID " +
                    "WHERE fg.FILM_ID IN (:filmIds)";
    private static final String FILM_BY_DIRECTOR_QUERY_ORDER_BY_RELEASE_DATE =
            "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
                    "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME, d.DIRECTOR_ID, d.DIRECTOR_NAME " +
                    "FROM FILMS f " +
                    "INNER JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE d.DIRECTOR_ID = ? " +
                    "ORDER BY f.RELEASE_DATE";
    private static final String FILM_BY_DIRECTOR_QUERY_ORDER_BY_LIKES =
            "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
                    "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME, d.DIRECTOR_ID, d.DIRECTOR_NAME " +
                    "FROM FILMS f " +
                    "INNER JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
                    "LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES GROUP BY FILM_ID) fl " +
                    "ON f.FILM_ID = fl.FILM_ID " +
                    "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
                    "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "WHERE d.DIRECTOR_ID = ? " +
                    "ORDER BY LIKES DESC";
    private static final String FOR_FILMS_QUERY =
            "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
            "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME, d.DIRECTOR_ID, d.DIRECTOR_NAME " +
            "FROM FILMS f " +
            "INNER JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
            "LEFT JOIN FILMS_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID " +
            "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "WHERE d.DIRECTOR_NAME LIKE ?" +
            "OR f.FILM_NAME LIKE ?";
    private static final String POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY = "SELECT f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, " +
            "f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME, COALESCE(fl.LIKES, 0) AS LIKES " +
            "FROM FILMS f " +
            "LEFT JOIN MPA_RATINGS m ON f.MPA_ID = m.MPA_ID " +
            "LEFT JOIN (SELECT FILM_ID, COUNT(FILM_ID) AS LIKES FROM FILMS_LIKES GROUP BY FILM_ID) fl " +
            "ON f.FILM_ID = fl.FILM_ID " +
            "LEFT JOIN FILMS_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
            "LEFT JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID " +
            "WHERE (? IS NULL OR g.GENRE_ID = ?) AND (? IS NULL OR EXTRACT(YEAR FROM f.RELEASE_DATE) = ?) " +
            "GROUP BY f.FILM_ID, f.FILM_NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.MPA_NAME " +
            "ORDER BY LIKES DESC LIMIT ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FOR_ALL_FILMS_QUERY);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = findOne(FOR_FILM_BY_ID_QUERY, id);
        film.setGenres(getGenresByFilm(id));
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        Collection<Film> films = findMany(TOP_FILMS_QUERY, count);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }
        }
        return films;
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
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public void delete(Integer id) {
        delete(DELETE_QUERY, id);
    }

    public boolean existsById(int filmId) {
        String query = "SELECT COUNT(*) FROM FILMS WHERE FILM_ID = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, filmId);
        return count != null && count > 0;
    }


    @Override
    public Collection<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (sortBy.equals("likes")) {
            return findMany(FILM_BY_DIRECTOR_QUERY_ORDER_BY_LIKES, directorId);
        }

        return findMany(FILM_BY_DIRECTOR_QUERY_ORDER_BY_RELEASE_DATE, directorId);
    }

    private Map<Integer, Set<Genre>> getAllGenres() {
        Map<Integer, Set<Genre>> genres = new HashMap<>();
        return jdbc.query(ALL_GENRES_FILMS_QUERY, (ResultSet rs) -> {
            while (rs.next()) {
                Integer filmId = rs.getInt("FILM_ID");
                Integer genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                genres.computeIfAbsent(filmId, k -> new HashSet<>()).add(new Genre(genreId, genreName));
            }
            return genres;
        });
    }

    private Set<Genre> getGenresByFilm(Integer filmId) {
        return jdbc.query(GENRES_BY_FILM_QUERY, (ResultSet rs) -> {
            Set<Genre> genres = new HashSet<>();
            while (rs.next()) {
                Integer genreId = rs.getInt("GENRE_ID");
                String genreName = rs.getString("GENRE_NAME");
                genres.add(new Genre(genreId, genreName));
            }
            return genres;
        }, filmId);
    }

    @Override
    public Collection<Film> search(String query, String by) {
        String searchFilmName = "";
        String searchFilmDirector = "";

        if (by.contains("title")) {
            searchFilmName = "%" + query + "%";
        }

        if (by.contains("director")) {
            searchFilmDirector = "%" + query + "%";
        }

        Collection<Film> films = findMany(FOR_FILMS_QUERY, searchFilmDirector, searchFilmName);

        films.forEach(film -> film.setGenres(getGenresByFilm(film.getId())));

        return films;
    }

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        List<Film> films = findMany(COMMON_FILMS_QUERY, userId, friendId);

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Integer, Set<Genre>> genresByFilmId = getGenresByFilmIds(filmIds);

        films.forEach(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), new HashSet<>())));

        return films;
    }

    private Map<Integer, Set<Genre>> getGenresByFilmIds(List<Integer> filmIds) {
        String sql = GENRES_BY_FILM_IDS_QUERY.replace(":filmIds", filmIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));

        return jdbc.query(sql, rs -> {
            Map<Integer, Set<Genre>> genresMap = new HashMap<>();
            while (rs.next()) {
                Integer filmId = rs.getInt("FILM_ID");
                Genre genre = new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
                genresMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return genresMap;
        });
    }

    @Override
    public Collection<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        Collection<Film> films = findMany(POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY, genreId, genreId, year, year, count);
        Map<Integer, Set<Genre>> genres = getAllGenres();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(genres.get(film.getId()));
            }
        }
        return films;
    }
}