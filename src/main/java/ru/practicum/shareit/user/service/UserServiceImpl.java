package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        checkDuplicateEmail(userDto.getEmail());
        User user = userMapper.convertToUser(userDto);
        userRepository.create(user);
        return userMapper.convertToUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(userMapper::convertToUserDto)
                .toList();
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.getById(id);
        return userMapper.convertToUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        checkDuplicateEmail(userDto.getEmail());
        userDto.setId(userId);
        User user = userMapper.convertToUser(userDto);
        userRepository.update(userId, user);
        return userMapper.convertToUserDto(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private void checkDuplicateEmail(String email) {
        if (userRepository.getAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            throw new DuplicationException("Email уже используется: " + email);
        }
    }
}
