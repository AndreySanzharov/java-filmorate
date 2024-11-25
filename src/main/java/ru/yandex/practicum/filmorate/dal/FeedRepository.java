package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

@Repository
public class FeedRepository extends BaseRepository<Feed> {
    private static final String INSERT_EVENT_QUERY = "INSERT INTO FEED (TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_EVENTS_BY_USER_ID_QUERY = "SELECT * FROM FEED WHERE USER_ID = ?";

    public FeedRepository(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    public void addEvent(Feed feed) {
        update(INSERT_EVENT_QUERY, feed.getTimestamp(), feed.getUserId(), feed.getEventType(), feed.getOperation(), feed.getEntityId());
    }

    public List<Feed> findEventsByUserId(Integer userId) {
        return findMany(FIND_EVENTS_BY_USER_ID_QUERY, userId);
    }
}
