package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final LikesRepository likesRepository;

    public Collection<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Optional<Film> getFilmById(Integer id) {
        return filmRepository.getFilmById(Long.valueOf(id));
    }

    public Film createFilm(Film film) {
        return filmRepository.create(film);
    }

    public Film updateFilm(Film film) {
        return filmRepository.update(film);
    }

    public void deleteFilm(Integer id) {
        filmRepository.delete(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        likesRepository.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likesRepository.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmRepository.getPopularFilms(count);
    }
}
