package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public class LikesRepository extends BaseRepository<Film> {
    private static final String ADD_LIKE_QUERY = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String LIKES_BY_USER_ID_QUERY = "SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?"; // новый запрос для получения лайков

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Integer filmId, Integer userId) {
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

    public List<Integer> getLikesByUserId(Integer userId) {
        return jdbc.query(LIKES_BY_USER_ID_QUERY, (rs, rowNum)
                -> rs.getInt("FILM_ID"), userId);
    }
}
