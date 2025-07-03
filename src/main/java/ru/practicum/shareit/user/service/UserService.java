package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto getById(Long id);

    UserDto update(Long userId, UserDto userDto);

    void deleteById(Long id);
}
