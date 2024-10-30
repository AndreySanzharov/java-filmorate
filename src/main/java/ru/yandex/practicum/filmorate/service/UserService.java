package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;


    public User addFriend(int userId, int friendId) {
        friendshipRepository.addFriend(userId, friendId);
        log.info("Добавление друга");
        return getUserById(userId);
    }

    public User create(User user) {
        log.info("Создание пользователя");
        return userStorage.create(user);
    }


    public User update(User newUser) {
        log.info("Обновление пользователя");
        return userStorage.update(newUser);
    }

    public User deleteFriend(int userId, int friendId) {
        log.info("Удаление друга");
        friendshipRepository.deleteFriend(userId, friendId);
        return getUserById(userId);
    }

    public Collection<User> getFriendsById(int id) {
        log.info("Получение друзей по id");
        return friendshipRepository.getFriendsById(id);
    }

    public Collection<User> getMutualFriendsById(int id, int otherId) {
        log.info("Получение общих друзей");
        return friendshipRepository.getMutualFriendsById(id, otherId);
    }

    public Collection<User> findAll() {
        log.info("Нахождение всех пользователей");
        return userStorage.findAll();
    }

    public User getUserById(int userId) {
        log.info("Получение пользователя по id");
        return userStorage.getUserById(userId).get();
    }
}
