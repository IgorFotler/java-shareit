package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserDto inputUser = new UserDto(null, "qwerty", "qwerty@example.com");
        UserDto returnedUser = new UserDto(1L, "qwerty", "qwerty@example.com");

        Mockito.when(userService.create(any(UserDto.class))).thenReturn(returnedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedUser.getId()))
                .andExpect(jsonPath("$.name").value(returnedUser.getName()))
                .andExpect(jsonPath("$.email").value(returnedUser.getEmail()));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        UserDto returnedUser = new UserDto(1L, "qwerty", "qwerty@example.com");

        Mockito.when(userService.getById(1L)).thenReturn(returnedUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(returnedUser.getId()))
                .andExpect(jsonPath("$.name").value(returnedUser.getName()))
                .andExpect(jsonPath("$.email").value(returnedUser.getEmail()));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Long userId = 1L;
        UserDto inputUser = new UserDto(null, "qwerty", "qwerty1@example.com");
        UserDto returnedUser = new UserDto(userId, "qwerty", "qwerty1@example.com");

        Mockito.when(userService.update(eq(userId), any(UserDto.class))).thenReturn(returnedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("qwerty"))
                .andExpect(jsonPath("$.email").value("qwerty1@example.com"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        Mockito.verify(userService).deleteById(1L);
    }
}
