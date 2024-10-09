package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    User addFriend(int userId, int friendId);

    User deleteFriend(int userId, int friendId);

    User getUserById(int userId);

    List<User> getFriendsById(int id);
}
