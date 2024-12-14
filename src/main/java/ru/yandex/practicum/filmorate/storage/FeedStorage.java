package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void addEvent(Feed feed);

    List<Feed> findEventsByUserId(Integer userId);
}
