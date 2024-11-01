package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {
    private static final String ALL_MPA_QUERY = "SELECT * FROM MPA_RATINGS";
    private static final String GET_MPA_BY_ID_QUERY = "SELECT * FROM MPA_RATINGS WHERE MPA_ID = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Mpa> getAllMpa() {
        return findMany(ALL_MPA_QUERY);
    }

    public Optional<User> getMpaById(Integer mpaId) {
        return findOne(GET_MPA_BY_ID_QUERY, mpaId);
    }
}