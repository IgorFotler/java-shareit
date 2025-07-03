package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    BookingDto create(@RequestHeader(userIdHeader) Long userId, @RequestBody BookingDto bookingDto) {
        log.info("Получен HTTP-запрос на создание бронирования");
        BookingDto createdBooking = bookingService.create(userId, bookingDto);
        log.info("Успешно обработан HTTP-запрос на создание бронирования");
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    BookingDto update(@RequestHeader(userIdHeader) Long userId, @RequestBody Long bookingId, @RequestParam boolean approved) {
        log.info("Получен HTTP-запрос на подтверждение бронирования");
        BookingDto updatedBooking = bookingService.update(userId, bookingId, approved);
        log.info("Успешно обработан HTTP-запрос на подтверждение бронирования");
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    BookingDto getById(@PathVariable Long bookingId, @RequestHeader(userIdHeader) Long userId) {
        log.info("Получен HTTP-запрос на получение бронирования по id: {}", bookingId);
        BookingDto bookingDto = bookingService.getById(bookingId, userId);
        log.info("Успешно обработан HTTP-запрос на получение бронирования по id: {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    List<BookingDto> getAllByUser(@RequestHeader(userIdHeader) Long userId,
                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен HTTP-запрос на получение бронирований пользователя с id: {}", userId);
        List<BookingDto> allByUser = bookingService.getAllByUser(userId, state);
        log.info("Успешно обработан HTTP-запрос на получение бронирований пользователя с id: {}", userId);
        return allByUser;
    }

    @GetMapping("/owner")
    List<BookingDto> getAllByOwner(@RequestHeader(userIdHeader) Long userId,
                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен HTTP-запрос на получение бронирований вещей пользователя с id: {}", userId);
        List<BookingDto> allByOwner = bookingService.getAllByOwner(userId, state);
        log.info("Получен HTTP-запрос на получение бронирований вещей пользователя с id: {}", userId);
        return allByOwner;
    }
}
