package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Repository
public class FeedRepository extends BaseRepository<Feed> implements FeedStorage {
    private static final String INSERT_EVENT_QUERY = "INSERT INTO FEED (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_EVENTS_BY_USER_ID_QUERY = "SELECT * FROM FEED WHERE USER_ID = ?";

    public FeedRepository(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addEvent(Feed feed) {
        update(INSERT_EVENT_QUERY, feed.getTimestamp(), feed.getUserId(), feed.getEventType(), feed.getOperation(), feed.getEntityId());
    }

    @Override
    public List<Feed> findEventsByUserId(Integer userId) {
        return findMany(FIND_EVENTS_BY_USER_ID_QUERY, userId);
    }
}
