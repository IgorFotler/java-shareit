package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.DuplicationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userMapper);
    }

    @Test
    void createUserTest() {
        UserDto inputDto = new UserDto(null, "John", "john@example.com");
        User savedUser = new User(1L, "John", "john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.create(inputDto);

        assertNotNull(result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserDuplicationException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(new User());

        UserDto inputDto = new UserDto(null, "John", "john@example.com");

        assertThrows(DuplicationException.class, () -> userService.create(inputDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getByIdUserNotFoundException() {
        when(userRepository.getById(99L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void updateUserTest() {
        User existingUser = new User(1L, "OldName", "old@example.com");
        User updatedUser = new User(1L, "NewName", "new@example.com");
        UserDto inputDto = new UserDto(null, "NewName", "new@example.com");

        when(userRepository.getById(1L)).thenReturn(existingUser);
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.update(1L, inputDto);

        assertEquals(1L, result.getId());
        assertEquals("NewName", result.getName());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateUserDuplicationException() {
        User existingUser = new User(1L, "OldName", "old@example.com");
        UserDto inputDto = new UserDto(null, "Name", "duplicate@example.com");

        when(userRepository.getById(1L)).thenReturn(existingUser);
        when(userRepository.findByEmail("duplicate@example.com")).thenReturn(new User());

        assertThrows(DuplicationException.class, () -> userService.update(1L, inputDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserTest() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }
}