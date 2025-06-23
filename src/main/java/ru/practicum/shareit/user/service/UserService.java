package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto update(Long userId, UserDto userDto);

    void deleteById(Long id);
}
