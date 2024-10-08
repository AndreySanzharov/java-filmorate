package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Cоздание фильма {}", film);
        //validate(film, false);

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен");
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Ошибка валидации фильма: id должен быть указан.");
            throw new ValidationException("Id должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            //validate(newFilm, true);
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм успешно обновлен");
            return oldFilm;
        }
        throw new NotFoundException("Пост с id " + newFilm.getId() + " не найден.");
    }

    private long getNextId() {
        Set<Long> allId = films.keySet();
        long maxId = 0;
        for (long id : allId) {
            if (id > maxId) {
                maxId = id;
            }
        }
        long currentID = ++maxId;
        return currentID;
    }
}
