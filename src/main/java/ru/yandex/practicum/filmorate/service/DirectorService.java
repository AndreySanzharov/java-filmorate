package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage repository;

    public Collection<Director> getDirectors() {
        return repository.getList();
    }

    public Director getDirectorsById(int id) {
        return repository.getById(id);
    }

    public Director addDirector(Director director) {
        return repository.create(director);
    }

    public Director updateDirector(Director director) {
        repository.getById(director.getId());

        return repository.update(director);
    }

    public void deleteDirector(int id) {
        repository.delete(id);
    }
}
