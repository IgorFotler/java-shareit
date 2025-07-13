package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserAndItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    private BookingDto bookingDto;
    private BookingWithUserAndItemDto bookingWithUserAndItemDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, 1L, BookingStatus.WAITING);
        bookingWithUserAndItemDto = new BookingWithUserAndItemDto(
                1L,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                new Item(1L, "Вещь", "Описание", true, null, null),
                new User(1L, "Имя", "email@example.com"),
                BookingStatus.WAITING
        );
    }

    @Test
    void createBookingTest() throws Exception {
        Mockito.when(bookingService.create(Mockito.anyLong(), any())).thenReturn(bookingWithUserAndItemDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingWithUserAndItemDto.getId()));
    }

    @Test
    void updateBookingTest() throws Exception {
        Mockito.when(bookingService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(bookingWithUserAndItemDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingWithUserAndItemDto.getId()));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito.when(bookingService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingWithUserAndItemDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingWithUserAndItemDto.getId()));
    }

    @Test
    void getAllByUserTest() throws Exception {
        Mockito.when(bookingService.getAllByUser(Mockito.anyLong(), Mockito.anyString())).thenReturn(List.of(bookingWithUserAndItemDto));

        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingWithUserAndItemDto.getId()));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        Mockito.when(bookingService.getAllByOwner(Mockito.anyLong(), Mockito.anyString())).thenReturn(List.of(bookingWithUserAndItemDto));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingWithUserAndItemDto.getId()));
    }
}