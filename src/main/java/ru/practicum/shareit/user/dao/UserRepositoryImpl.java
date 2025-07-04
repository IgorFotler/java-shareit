package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            String errorMessage = String.format("Пользователь с id %d не найден", id);
            log.error(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }
        return user;
    }


    @Override
    public void deleteById(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            String errorMessage = String.format("Пользователь с id %d не найден", id);
            log.error(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }
    }
}
