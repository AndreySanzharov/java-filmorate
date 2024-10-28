package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?) RETURNING id";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'PENDING')";
    // private static final String CONFIRM_FRIEND_QUERY = "UPDATE friendships SET status = 'CONFIRMED' WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT * FROM users u JOIN friendships f ON u.id = f.friend_id WHERE f.user_id = ? AND f.status = 'CONFIRMED'";
    private static final String GET_MUTUAL_FRIENDS_QUERY = "SELECT * FROM users u JOIN friendships f1 ON u.id = f1.friend_id JOIN friendships f2 ON u.id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = 'CONFIRMED' AND f2.status = 'CONFIRMED'";

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, userMapper);
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Optional.ofNullable(keyHolder.getKey()).map(Number::longValue).orElseThrow(() -> new NotFoundException("Failed to create user")));
        return user;
    }

    @Override
    public User update(User user) {
        int rowsUpdated = jdbcTemplate.update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("User not found for update");
        }
        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, userMapper, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User addFriend(int userId, int friendId) {
        jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId);
        return getUserById(userId).orElseThrow(() -> new NotFoundException("User not found for adding friend"));
    }

//     @Override
//    public User confirmFriend(int userId, int friendId) {
//        jdbcTemplate.update(CONFIRM_FRIEND_QUERY, userId, friendId);
//        return getUserById(userId).orElseThrow(() -> new NotFoundException("User not found for confirming friend"));
//    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        jdbcTemplate.update(DELETE_FRIEND_QUERY, userId, friendId);
        return getUserById(userId).orElseThrow(() -> new NotFoundException("User not found for deleting friend"));
    }

    @Override
    public List<User> getFriendsById(int userId) {
        return jdbcTemplate.query(GET_FRIENDS_QUERY, userMapper, userId);
    }

    @Override
    public List<User> getMutualFriendsById(int userId, int otherId) {
        return jdbcTemplate.query(GET_MUTUAL_FRIENDS_QUERY, userMapper, userId, otherId);
    }
}
