package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final LikesRepository likesRepository;
    @Autowired
    private final FilmRepository filmRepository;

    public List<Film> getRecommendations(Integer userId) {
        User targetUser = userRepository.getUserById(userId); // пользователь, для которого составляется рекомендация
        List<User> allUsers = userRepository.findAll();
        List<Film> allFilms = filmRepository.findAll();
        Map<User, Integer> userSimilarityScores = new HashMap<>();
        //получаем все лайки одного пользователя
        Set<Integer> targetUserLikes = new HashSet<>(likesRepository.getLikesByUserId(userId));
        // получить все лайки всех пользователей
        Map<Integer, Set<Integer>> allUserLikes = likesRepository.getAllLikes();

        // найти пользователей с максимальным кол-вом пересечений по лайкам
        for (Map.Entry<Integer, Set<Integer>> entry : allUserLikes.entrySet()) {
            Integer currentUserId = entry.getKey();
            Set<Integer> otherUserLikes = entry.getValue();
            // пропускаем текущего пользователя
            if (!currentUserId.equals(userId)) {
                int intersectionCount = countIntersection(targetUserLikes, otherUserLikes);
                if (intersectionCount > 0) {
                    userSimilarityScores.put(allUsers.stream()
                            .filter(user -> user.getId().equals(currentUserId))
                            .findFirst()
                            .orElse(null), intersectionCount);
                }
            }
        }

        // сортировка по кол-ву пересечений
        List<Map.Entry<User, Integer>> sortedUsers = new ArrayList<>(userSimilarityScores.entrySet());
        sortedUsers.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // определить фильмы, которые один пролайкал, а другой нет
        // и рекомендовать фильмы, которым поставил лайк пользователь
        // с похожими вкусами, а тот, для кого составляется рекомендация
        // еще не поставил
        Set<Film> recommendedFilms = new HashSet<>();
        for (Map.Entry<User, Integer> entry : sortedUsers) {
            User similarUser = entry.getKey();
            Set<Integer> similarUserLikes = new HashSet<>(likesRepository.getLikesByUserId(similarUser.getId()));
            for (Integer filmId : similarUserLikes) {
                if (!targetUserLikes.contains(filmId)) {
                    for (Film film : allFilms) {
                        if (film.getId().equals(filmId)) {
                            recommendedFilms.add(film);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(recommendedFilms);
    }

    // метод для подсчета пересечений по лайкам
    private int countIntersection(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }
}
