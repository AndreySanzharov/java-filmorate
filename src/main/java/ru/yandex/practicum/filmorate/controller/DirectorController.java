package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorsService;

    @GetMapping
    public List<Director> getDirectors() {
        return directorsService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorsById(@PathVariable int id) {
        return directorsService.getDirectorsById(id);
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        return directorsService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorsService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorsService.deleteDirector(id);
    }
}
