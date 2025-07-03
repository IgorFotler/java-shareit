package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Получен HTTP-запрос на создание пользователя: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("Успешно обработан HTTP-запрос на создание пользователя: {}", userDto);
        return createdUser;
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Получен HTTP-запрос на получение пользователя по id: {}", id);
        UserDto userDto = userService.getById(id);
        log.debug("Найденный пользователь: {}", userDto);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен HTTP-запрос на обновление пользователя: {}", userDto);
        userService.update(userId, userDto);
        log.info("Успешно выполнен HTTP-запрос на обновление пользователя: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("Получен HTTP-запрос на удаление пользователя по id: {}", id);
        userService.deleteById(id);
        log.info("Успешно выполнен HTTP-запрос на удаление пользователя с id {}", id);
    }
}
