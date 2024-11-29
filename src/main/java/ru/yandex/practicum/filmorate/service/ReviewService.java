package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final FeedRepository feedService;

    public Review addReview(Review review) {
        // Проверка существования пользователя
        if (!userRepository.existsById(review.getUserId())) {
            throw new NotFoundException("Пользователь с ID " + review.getUserId() + " не найден.");
        }

        //проверка существования фильма
        if (!filmRepository.existsById(review.getFilmId())) {
            throw new NotFoundException("Фильм с ID " + review.getFilmId() + " не найден.");
        }

        validateReview(review);
        Review createdReview = reviewRepository.createReview(review);
        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(createdReview.getUserId())
                .eventType("REVIEW")
                .operation("ADD")
                .entityId(createdReview.getReviewId())
                .build());

        return createdReview;
    }

    public Review updateReview(Review review) {
        validateReview(review);
        ensureReviewExists(review.getReviewId());
        Review updatedReview = reviewRepository.updateReview(review);
        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(updatedReview.getUserId())
                .eventType("REVIEW")
                .operation("UPDATE")
                .entityId(updatedReview.getReviewId())
                .build());
        return updatedReview;
    }

    public void deleteReview(Integer reviewId) {
        ensureReviewExists(reviewId);
        Review review = reviewRepository.findReviewById(reviewId);
        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("REMOVE")
                .entityId(reviewId)
                .build());
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
