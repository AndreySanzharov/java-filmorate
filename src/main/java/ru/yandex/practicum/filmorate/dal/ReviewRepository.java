package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Repository
public class ReviewRepository extends BaseRepository<Review> {

    private static final String INSERT_REVIEW = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
            "VALUES (?, ?, ?, ?, 0)";
    private static final String UPDATE_REVIEW = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
    private static final String DELETE_REVIEW = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String FIND_REVIEW_BY_ID = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
    private static final String FIND_REVIEWS_BY_FILM = "SELECT * FROM REVIEWS WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";
    private static final String FIND_ALL_REVIEWS = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC LIMIT ?";
    private static final String ADD_LIKE = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
    private static final String ADD_DISLIKE = "INSERT INTO REVIEW_DISLIKES (REVIEW_ID, USER_ID) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String REMOVE_DISLIKE = "DELETE FROM REVIEW_DISLIKES WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String INCREMENT_USEFUL = "UPDATE REVIEWS SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?";
    private static final String DECREMENT_USEFUL = "UPDATE REVIEWS SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Review createReview(Review review) {
        Integer id = insert(INSERT_REVIEW, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId());
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    public Review updateReview(Review review) {
        update(UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findReviewById(review.getReviewId());
    }


    public void deleteReview(Integer reviewId) {
        delete(DELETE_REVIEW, reviewId);
    }

    public Review findReviewById(Integer reviewId) {
        return findOne(FIND_REVIEW_BY_ID, reviewId);
    }

    public List<Review> findReviewsByFilm(Integer filmId, Integer count) {
        return findMany(FIND_REVIEWS_BY_FILM, filmId, count);
    }

    public List<Review> findAllReviews(Integer count) {
        return findMany(FIND_ALL_REVIEWS, count);
    }

    public void addLike(Integer reviewId, Integer userId) {
        update(ADD_LIKE, reviewId, userId);
        update(INCREMENT_USEFUL, reviewId);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        update(REMOVE_LIKE, reviewId, userId);
        update(DECREMENT_USEFUL, reviewId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        update(ADD_DISLIKE, reviewId, userId);
        update(DECREMENT_USEFUL, reviewId);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        update(REMOVE_DISLIKE, reviewId, userId);
        update(INCREMENT_USEFUL, reviewId);
    }
}