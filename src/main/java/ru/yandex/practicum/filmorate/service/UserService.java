package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        return userStorage.addFriend(userId, friendId);
    }

//    public User confirmFriend(int userId, int friendId) {
//        return userStorage.confirmFriend(userId, friendId);
//    }

    public User deleteFriend(int userId, int friendId) {
        return userStorage.deleteFriend(userId, friendId);
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
        return userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }
}
