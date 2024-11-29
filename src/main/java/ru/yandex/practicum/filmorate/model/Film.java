package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

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
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Genre> genres = new LinkedHashSet<>();
    private List<Director> directors = new ArrayList<>();
    private Mpa mpa;

    @AssertTrue(message = "Дата релиза должна быть не раньше 28 декабря 1895 года.")
    public boolean isValidReleaseDate() {
        return releaseDate == null || !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }
}
