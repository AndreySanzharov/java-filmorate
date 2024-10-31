package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository implements UserStorage {
    private static final String INSERT_QUERY = "INSERT INTO " + "USERS (EMAIL, LOGIN, USERNAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String UPDATE_QUERY = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, USERNAME = ?, " + "BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String USER_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";
    private static final String EXISTS_USER_QUERY = "SELECT COUNT(1) FROM USERS WHERE USER_ID = ?";


    public UserRepository(JdbcTemplate jdbc, RowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        Integer id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(Long.valueOf(id));
        return user;
    }

    @Override
    public User update(User newUser) {
        Integer count = jdbc.queryForObject(EXISTS_USER_QUERY, Integer.class, newUser.getId());
        if (count == null || count == 0) {
            throw new NotFoundException("Пользователь с ID " + newUser.getId() + " не найден");
        }
        update(UPDATE_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId());
        return newUser;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return findOne(USER_BY_ID_QUERY, userId);
    }

    @Override
    public void delete(Integer id) {
        delete(DELETE_QUERY, id);
    }
}