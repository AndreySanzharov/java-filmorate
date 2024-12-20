package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {

    private static final String INSERT_REVIEW = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
            "VALUES (?, ?, ?, ?, 0)";
    private static final String UPDATE_REVIEW = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
    private static final String DELETE_REVIEW = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String FIND_REVIEW_BY_ID = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String FIND_REVIEWS_BY_FILM =
            "SELECT r.REVIEW_ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, r.USEFUL " +
                    "FROM REVIEWS r " +
                    "WHERE r.FILM_ID = ? " +
                    "ORDER BY r.USEFUL DESC LIMIT ?";

    private static final String FIND_ALL_REVIEWS = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC LIMIT ?";
    private static final String ADD_LIKE = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, TRUE)";
    private static final String ADD_DISLIKE = "INSERT INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, FALSE)";
    private static final String REMOVE_LIKE = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String REMOVE_DISLIKE = "DELETE FROM REVIEWS_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String INCREMENT_USEFUL = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
    private static final String DECREMENT_USEFUL = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review createReview(Review review) {
        Integer id = insert(INSERT_REVIEW, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId());
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findReviewById(review.getReviewId());
    }

    @Override
    public void deleteReview(Integer reviewId) {
        delete(DELETE_REVIEW, reviewId);
    }

    @Override
    public Review findReviewById(Integer reviewId) {
        return findOne(FIND_REVIEW_BY_ID, reviewId);
    }

    @Override
    public List<Review> findReviewsByFilm(Integer filmId, Integer count) {
        return findMany(FIND_REVIEWS_BY_FILM, filmId, count);
    }

    @Override
    public List<Review> findAllReviews(Integer count) {
        return findMany(FIND_ALL_REVIEWS, count);
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        if (!isReviewLikeExists(reviewId, userId)) {
            update(ADD_LIKE, reviewId, userId);
            update(INCREMENT_USEFUL, reviewId);
        }
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        String checkQuery = "SELECT IS_LIKE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        Boolean existingReaction = jdbc.query(checkQuery, rs -> rs.next() ? rs.getBoolean("IS_LIKE") : null, reviewId, userId);

        if (existingReaction == null) {
            // Если реакции нет, добавляем дизлайк и уменьшаем полезность
            update(ADD_DISLIKE, reviewId, userId);
            update(DECREMENT_USEFUL, reviewId);
        } else if (existingReaction) {
            // Если это лайк, меняем его на дизлайк, уменьшаем полезность дважды
            String updateQuery = "UPDATE REVIEWS_LIKES SET IS_LIKE = FALSE WHERE REVIEW_ID = ? AND USER_ID = ?";
            update(updateQuery, reviewId, userId);
            update(DECREMENT_USEFUL, reviewId); // Уменьшаем полезность за удаление лайка
            update(DECREMENT_USEFUL, reviewId); // Уменьшаем полезность за добавление дизлайка
        }
        // Если это уже дизлайк, ничего не делаем
    }

    @Override
    public void removeLike(Integer reviewId, Integer userId) {
        update(REMOVE_LIKE, reviewId, userId);
        update(DECREMENT_USEFUL, reviewId);
    }

    @Override
    public void removeDislike(Integer reviewId, Integer userId) {
        update(REMOVE_DISLIKE, reviewId, userId);
        update(INCREMENT_USEFUL, reviewId);
    }

    private boolean isReviewLikeExists(Integer reviewId, Integer userId) {
        String checkQuery = "SELECT COUNT(*) FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
        Integer count = jdbc.queryForObject(checkQuery, Integer.class, reviewId, userId);
        return count > 0;
    }
}