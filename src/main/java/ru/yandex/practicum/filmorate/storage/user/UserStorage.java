package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    User addFriend(int userId, int friendId);

    User deleteFriend(int userId, int friendId);

    Optional<User> getUserById(int userId);

    List<User> getFriendsById(int id);

    List<User> getMutualFriendsById(int id, int otherId);
}
