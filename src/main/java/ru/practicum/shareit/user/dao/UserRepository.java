package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> getAll();

    User getById(Long id);

    User update(Long userId, User user);

    void deleteById(Long id);
}
