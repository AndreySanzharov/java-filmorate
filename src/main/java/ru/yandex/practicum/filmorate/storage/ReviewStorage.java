package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Integer reviewId);

    Review findReviewById(Integer reviewId);

    List<Review> findReviewsByFilm(Integer filmId, Integer count);

    List<Review> findAllReviews(Integer count);

    void addLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void removeLike(Integer reviewId, Integer userId);

    void removeDislike(Integer reviewId, Integer userId);
}
