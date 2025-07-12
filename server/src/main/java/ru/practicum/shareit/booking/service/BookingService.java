package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserAndItemDto;

import java.util.List;

public interface BookingService {
    BookingWithUserAndItemDto create(Long userId, BookingDto bookingDto);

    BookingWithUserAndItemDto update(Long userId, Long bookingId, boolean approved);

    BookingWithUserAndItemDto getById(Long bookingId, Long userId);

    List<BookingWithUserAndItemDto> getAllByUser(Long userId, String state);

    List<BookingWithUserAndItemDto> getAllByOwner(Long userId, String state);
}
