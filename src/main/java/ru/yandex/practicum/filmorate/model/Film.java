package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.deserializers.StringToDurationDeserializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonDeserialize(using = StringToDurationDeserializer.class)
    private Duration duration;
}
