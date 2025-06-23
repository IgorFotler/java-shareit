package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    List<User> getAll();

    User getById(Long id);

    void deleteById(Long id);
}
