package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Slf4j
@Repository
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {
    private static final String SELECT_LIST = "SELECT DIRECTOR_ID, DIRECTOR_NAME FROM DIRECTORS";

    private static final String SELECT_BY_ID = "SELECT DIRECTOR_ID, DIRECTOR_NAME FROM DIRECTORS d WHERE d.DIRECTOR_ID = ?";

    private static final String CREATE = "INSERT INTO DIRECTORS (DIRECTOR_NAME) VALUES (?)";

    private static final String UPDATE = "UPDATE DIRECTORS SET DIRECTOR_NAME=? WHERE DIRECTOR_ID=?";

    private static final String DELETE = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID=?";

    private static final String GET_DIRECTOR_BY_FILM = "SELECT d.DIRECTOR_ID, d.DIRECTOR_NAME " +
            "FROM FILMS_DIRECTORS fd " +
            "INNER JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
            "WHERE fd.FILM_ID = ?";

    private static final String DETELE_FILM_DIRECTOR = "DELETE FROM FILMS_DIRECTORS WHERE FILM_ID=?";

    private static final String CREATE_FILM_DIRECTOR = "INSERT INTO FILMS_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> getList() {
        return findMany(SELECT_LIST);
    }

    @Override
    public Director getById(int id) {
        return findOne(SELECT_BY_ID, id);
    }

    @Override
    public Director create(Director director) {
        Integer id = insert(CREATE, director.getName());

        return new Director(id, director.getName());
    }

    @Override
    public Director update(Director director) {
        update(UPDATE,
                director.getName(),
                director.getId()
        );

        return director;
    }

    @Override
    public void delete(int id) {
        delete(DELETE, id);
    }

    @Override
    public Collection<Director> getDirectorByFilm(int filmId) {
        return findMany(GET_DIRECTOR_BY_FILM, filmId);
    }

    @Override
    public void deleteFilmDirector(int filmId) {
        delete(DETELE_FILM_DIRECTOR, filmId);
    }

    @Override
    public void createFilmDirector(int filmId, int directorId) {
        update(CREATE_FILM_DIRECTOR, filmId, directorId);
    }
}
