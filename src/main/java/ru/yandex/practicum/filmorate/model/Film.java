package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(max = 200, message = "Максимальная длина описания - 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза должна быть указана.")
    @Past(message = "Самая ранняя дата релиза может быть: 28.12.1895.")
    private LocalDate releaseDate;

    @PositiveOrZero(message = "Продолжительность должна быть положительным числом или нулем")
    private int duration;
}
