package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public Collection<Mpa> findAllMpa() {
        return mpaRepository.findAllMpa();
    }

    public Mpa getMpaById(Integer mpaId) {
        return mpaRepository.getMpaById(mpaId);
    }
}