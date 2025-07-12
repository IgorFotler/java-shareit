package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    ResponseEntity<Object> create(@RequestHeader(userIdHeader) Long userId, @RequestBody BookingDto bookingDto) {
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> update(@RequestHeader(userIdHeader) Long userId, @PathVariable Long bookingId, @RequestParam boolean approved) {
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> getById(@PathVariable Long bookingId, @RequestHeader(userIdHeader) Long userId) {
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    ResponseEntity<Object> getAllByUser(@RequestHeader(userIdHeader) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    ResponseEntity<Object> getAllByOwner(@RequestHeader(userIdHeader) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllByOwner(userId, state);
    }
}
