package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Integer id;
    private Integer reviewId;  // Уникальный идентификатор отзыва
    private String content;    // Текст отзыва
    private Boolean isPositive; // Тип отзыва (положительный/негативный)
    private Integer userId;    // Идентификатор пользователя
    private Integer filmId;    // Идентификатор фильма
    private Integer useful;    // Рейтинг полезности
}
