package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        checkDuplicateEmail(userDto.getEmail());
        User user = userMapper.convertToUser(userDto);
        user = userRepository.save(user);
        return userMapper.convertToUserDto(user);
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + id + " не найден"));
        return userMapper.convertToUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.getById(userId);
        if (!user.getEmail().equals(userDto.getEmail())) {
            checkDuplicateEmail(userDto.getEmail());
        }
        userDto.setId(userId);

        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());

        user = userRepository.save(user);
        return userMapper.convertToUserDto(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.findByEmail((email)) != null) {
            throw new DuplicationException("Email уже используется: " + email);
        }
    }
}
