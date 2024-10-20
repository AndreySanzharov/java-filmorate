package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentID = 0;

    @Override
    public Collection<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User create(User user) {
        log.info("Создание пользователя");

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление пользователя: {}", newUser.getId());
        return getUserById(Math.toIntExact(newUser.getId()))
                .map(oldUser -> {
                    oldUser.setEmail(newUser.getEmail());
                    oldUser.setLogin(newUser.getLogin());
                    oldUser.setBirthday(newUser.getBirthday());

                    if (newUser.getName() == null || newUser.getName().isBlank()) {
                        oldUser.setName(newUser.getLogin());
                    } else {
                        oldUser.setName(newUser.getName());
                    }
                    log.info("Пользователь обновлен: {}", oldUser);
                    return oldUser;
                })
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден."));
    }

    @Override
    public User addFriend(int userId, int friendId) {
        log.info("Добавление в друзья пользователя с ID: {}", userId);
        User user = getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        User friend = getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг с ID " + friendId + " не найден."));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с ID {} добавлен в друзья пользователю с ID {}", friendId, userId);
        return user;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        log.info("Удаление из друзей пользователя с ID: {}", userId);
        User user = getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        User friend = getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг с ID " + friendId + " не найден."));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с ID {} удален из друзей пользователя с ID {}", friendId, userId);
        return user;
    }

    @Override
    public List<User> getFriendsById(int id) {
        log.info("Получение друзей пользователя с ID: {}", id);
        User user = getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден."));

        return user.getFriends().stream()
                .map(this::getUserById)
                .map(optionalUser -> optionalUser.orElseThrow(() -> new NotFoundException("Друг не найден.")))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(int userId) {
        log.info("Получение пользователя по id: {}", userId);

        // Проверяем наличие пользователя и выбрасываем исключение, если он не найден
        return Optional.ofNullable(users.get((long) userId))
                .or(() -> {
                    log.error("Пользователь с ID {} не найден", userId);
                    throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
                });
    }

    @Override
    public List<User> getMutualFriendsById(int userId, int otherId) {
        log.info("Получение общих друзей для пользователей с ID: {} и {}", userId, otherId);

        User user = getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        User otherUser = getUserById(otherId).orElseThrow(() -> new NotFoundException("Друг с ID " + otherId + " не найден."));

        List<User> mutualFriends = user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(this::getUserById)
                .map(optionalUser -> optionalUser.orElseThrow(() -> new NotFoundException("Друг не найден.")))
                .collect(Collectors.toList());

        log.info("Общие друзья найдены: {}", mutualFriends);
        return mutualFriends;
    }

    private long getNextId() {
        return ++currentID;
    }
}
