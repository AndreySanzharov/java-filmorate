package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Repository
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String ALL_USERS_QUERY = "SELECT * FROM USERS";
    private static final String USER_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO USERS " +
            "(EMAIL, LOGIN, USERNAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE USERS SET" +
            " EMAIL = ?, LOGIN = ?, USERNAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<User> findAll() {
        return findMany(ALL_USERS_QUERY);
    }

    @Override
    public User getUserById(Integer id) {
        return findOne(USER_BY_ID_QUERY, id);
    }

    @Override
    public User create(User user) {
        Integer id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void delete(Integer id) {
        delete(DELETE_QUERY, id);
    }

    public boolean existsById(int userId) {
        String query = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, userId);
        return count != null && count > 0;
    }

}