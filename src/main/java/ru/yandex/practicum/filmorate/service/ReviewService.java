package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review addReview(Review review) {
        validateReview(review);
        return reviewRepository.createReview(review);
    }

    public Review updateReview(Review review) {
        validateReview(review);
        ensureReviewExists(review.getReviewId());
        return reviewRepository.updateReview(review);
    }

    public void deleteReview(Integer reviewId) {
        ensureReviewExists(reviewId);
        reviewRepository.deleteReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        return reviewRepository.findReviewById(reviewId);
    }

    public List<Review> getReviews(Integer filmId, Integer count) {
        if (filmId != null) {
            return reviewRepository.findReviewsByFilm(filmId, count != null ? count : 10);
        } else {
            return reviewRepository.findAllReviews(count != null ? count : 10);
        }
    }

    public void addLike(Integer reviewId, Integer userId) {
        ensureReviewExists(reviewId);
        reviewRepository.addLike(reviewId, userId);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        ensureReviewExists(reviewId);
        reviewRepository.removeLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        ensureReviewExists(reviewId);
        reviewRepository.addDislike(reviewId, userId);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        ensureReviewExists(reviewId);
        reviewRepository.removeDislike(reviewId, userId);
    }

    private void ensureReviewExists(Integer reviewId) {
        reviewRepository.findReviewById(reviewId); // бросит исключение, если не найден
    }

    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new IllegalArgumentException("Содержимое отзыва не может быть пустым.");
        }
        if (review.getUserId() == null) {
            throw new IllegalArgumentException("Не указан пользователь.");
        }
        if (review.getFilmId() == null) {
            throw new IllegalArgumentException("Не указан фильм.");
        }
    }
}
