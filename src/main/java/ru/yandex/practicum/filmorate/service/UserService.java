package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;
    private final FeedRepository feedService;

    public void addFriend(Integer id, Integer friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipRepository.addFriend(id, friendId);
        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(id)
                .eventType("FRIEND")
                .operation("ADD")
                .entityId(friendId)
                .build());
    }

    public void deleteFriend(Integer id, Integer friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipRepository.deleteFriend(id, friendId);
        feedService.addEvent(Feed.builder()
                .timestamp(System.currentTimeMillis())
                .userId(id)
                .eventType("FRIEND")
                .operation("REMOVE")
                .entityId(friendId)
                .build());
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

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public List<Feed> findEventsByUserId(Integer userId) {
        if (userStorage.getUserById(userId) != null) {
            return new ArrayList<>(feedService.findEventsByUserId(userId));
        } else {
            throw new NotFoundException(String.format("пользователь с id %d не зарегистрирован.", userId));
        }
    }
}