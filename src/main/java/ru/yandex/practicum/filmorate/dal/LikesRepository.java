package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class LikesRepository extends BaseRepository<Film> {
    private static final String ADD_LIKE_QUERY = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String LIKES_BY_USER_ID_QUERY = "SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?"; // новый запрос для получения лайков
    private static final String LIKES_BY_USER_ID_AND_FILM_QUERY = "SELECT FILM_ID FROM FILMS_LIKES WHERE FILM_ID = ? AND USER_ID = ?"; // новый запрос для получения лайков
    private static final String ALL_LIKES_QUERY = "SELECT USER_ID, FILM_ID FROM FILMS_LIKES"; // новый запрос для получения всех лайков

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

    public List<Integer> getLikesByFilmAndUserId(Integer filmId, Integer userId) {
        return jdbc.query(LIKES_BY_USER_ID_AND_FILM_QUERY, (rs, rowNum)
                -> rs.getInt("FILM_ID"), filmId, userId);
    }

    public Map<Integer, Set<Integer>> getAllLikes() {
        List<LikeRecord> records = jdbc.query(
                ALL_LIKES_QUERY,
                (rs, rowNum) -> new LikeRecord(rs.getInt("USER_ID"), rs.getInt("FILM_ID"))
        );

        Map<Integer, Set<Integer>> userLikesMap = new HashMap<>();
        for (LikeRecord record : records) {
            userLikesMap
                    .computeIfAbsent(record.getUserId(), k -> new HashSet<>())
                    .add(record.getFilmId());
        }

        return userLikesMap;
    }

    private static class LikeRecord {
        private final Integer userId;
        private final Integer filmId;

        public LikeRecord(Integer userId, Integer filmId) {
            this.userId = userId;
            this.filmId = filmId;
        }

        public Integer getUserId() {
            return userId;
        }

        public Integer getFilmId() {
            return filmId;
        }
    }
}
