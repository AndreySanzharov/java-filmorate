package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    public User create(User user) {
        validate(user);
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);

    }

    public User deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> getFriendsById(int id) {
        return userStorage.getFriendsById(id);
    }

    public List<User> getMutualFriendsById(int id, int otherId) {
        return userStorage.getMutualFriendsById(id, otherId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin()); // Если имя пустое, назначаем логин как имя
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
    }
}
