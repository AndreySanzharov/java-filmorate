package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

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
        filmRepository.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmRepository.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmRepository.getPopularFilms(count);
    }
}
