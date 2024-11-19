package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {
    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание не может быть пустым.")
    @Size(max = 200, message = "Максимальная длина описания - 200 символов.")
    private String description;
    @NotNull(message = "Дата релиза должна быть указана.")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительным числом или нулем")
    private int duration;
    @JsonIgnore
    private List<Integer> likes = new ArrayList<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

    @AssertTrue(message = "Дата релиза должна быть не раньше 28 декабря 1895 года.")
    public boolean isValidReleaseDate() {
        return releaseDate == null || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}
