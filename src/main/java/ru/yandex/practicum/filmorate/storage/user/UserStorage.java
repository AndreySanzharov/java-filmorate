package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    User getUserById(int userId);

    User addFriend(int userId, int friendId);

    User deleteFriend(int userId, int friendId);
}
