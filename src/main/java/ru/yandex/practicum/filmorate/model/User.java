package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "Почта не должна быть пустой")
    @Email(message = "Email должен содержать символ '@'")
    private String email;
    @NotBlank(message = "Логин должен быть указан")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы.")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
    @JsonIgnore
    private Set<Integer> friends = new HashSet<>();
}
