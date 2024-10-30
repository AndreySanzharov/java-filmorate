package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;


    public User addFriend(int userId, int friendId) {
        friendshipRepository.addFriend(userId, friendId);
        return getUserById(userId);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User deleteFriend(int userId, int friendId) {
        friendshipRepository.deleteFriend(userId, friendId);
        return getUserById(userId);
    }

    public Collection<User> getFriendsById(int id) {
        return friendshipRepository.getFriendsById(id);
    }

    public Collection<User> getMutualFriendsById(int id, int otherId) {
        return friendshipRepository.getMutualFriendsById(id, otherId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId).get();
    }
}
