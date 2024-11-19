package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;

    public void addFriend(Integer id, Integer friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipRepository.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipRepository.deleteFriend(id, friendId);
    }

    public Collection<User> getMutualFriends(Integer id, Integer otherId) {
        return friendshipRepository.getMutualFriends(id, otherId);
    }

    public Collection<User> getFriendsById(Integer id) {
        getUserById(id);
        return friendshipRepository.getFriendsById(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        getUserById(user.getId());
        return userStorage.update(user);
    }

    public void delete(Integer id) {
        userStorage.delete(id);
    }

    private User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }
}