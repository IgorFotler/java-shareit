//package ru.practicum.shareit.user;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.practicum.shareit.user.dto.UserDto;
//
//@Slf4j
//@RestController
//@RequestMapping(path = "/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserClient userClient;
//
//    @PostMapping
//    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
//        return userClient.create(userDto);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> getById(@PathVariable Long id) {
//        return userClient.getById(id);
//    }
//
//    @PatchMapping("/{userId}")
//    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
//        return userClient.update(userId, userDto);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
//        return userClient.deleteById(id);
//    }
//}
