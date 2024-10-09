package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentID;

    @Override
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        validate(user, false); // Валидация после установки имени

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создание пользователя {}", user);
        log.info("Пользователь создан");
        return user;
    }

    @Override
    public User update(User newUser) {
        if (users.containsKey(newUser.getId())) {
            validate(newUser, true);
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            if (newUser.getName().isBlank() || newUser.getName() == null) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }

            log.info("Пользователь обновлен", oldUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден.");
    }

    @Override
    public User addFriend(int userId, int friendId) {
        log.info("Добавление друга");
        getUserById(userId).getFriends().add(friendId);
        getUserById(friendId).getFriends().add(userId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        log.info("Удаление друга");
        getUserById(userId).getFriends().remove(friendId);
        getUserById(friendId).getFriends().remove(userId);
        return getUserById(userId);
    }

    @Override
    public List<User> getFriendsById(int id) {
        log.info("Получение друзей по id");
        return users.values().stream().filter(user -> user.getFriends().contains(id)).collect(Collectors.toList());
    }

    @Override
    public User getUserById(int userId) {
        if (users.containsKey((long) userId)) {
            return users.get((long) userId);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validate(User user, boolean isUpdate) {
        if (isUpdate && (user.getId() == null)) {
            log.error("Ошибка валидации пользователя: id должен быть указан при обновлении.");
            throw new ValidationException("Id должен быть указан при обновлении.");
        }
    }

    private long getNextId() {
        Set<Long> allId = users.keySet();
        long maxId = 0;
        for (long id : allId) {
            if (id > maxId) {
                maxId = id;

            }
        }
        currentID = ++maxId;
        return currentID;
    }
}
