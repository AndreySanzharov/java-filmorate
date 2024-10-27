package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;


    public User addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        return getUserById(userId);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);

    }

    public User deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        return getUserById(userId);
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

    public User getUserById(int userId) {
        return userStorage.getUserById(userId).get();
    }
}
