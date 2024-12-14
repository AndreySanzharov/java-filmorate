package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Repository
public class MpaRepository extends BaseRepository<Mpa> implements MpaStorage {
    private static final String FOR_ALL_MPA_QUERY = "SELECT * FROM MPA_RATINGS";
    private static final String FOR_BY_ID_QUERY = "SELECT * FROM MPA_RATINGS WHERE MPA_ID = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return findMany(FOR_ALL_MPA_QUERY);
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        return findOne(FOR_BY_ID_QUERY, mpaId);
    }
}