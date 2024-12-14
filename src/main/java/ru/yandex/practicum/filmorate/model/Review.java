package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Integer reviewId;
    private String content;
    @NotNull(message = "isPositive не может быть пустым")
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private Integer useful;
}