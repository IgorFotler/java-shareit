package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserAndItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingWithUserAndItemDto create(Long userId, BookingDto bookingDto) {
        User booker = getUserById(userId);
        Item item = getItemById(bookingDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Вещь принадлежит данному пользователю");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Бронирование вещи недоступно");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть позже даты окончания");
        }
        Booking booking = bookingMapper.convertToBooking(bookingDto, item, booker);

        bookingRepository.save(booking);
        return bookingMapper.convertToBookingWithUserAndItemDto(booking);
    }


    @Override
    public BookingWithUserAndItemDto update(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Подтвердить бронирование может только владелец");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено");
        }

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new ValidationException("Бронирование отменено");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(booking);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(booking);
        }
        return bookingMapper.convertToBookingWithUserAndItemDto(booking);
    }

    @Override
    public BookingWithUserAndItemDto getById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        getUserById(userId);

        if (!booking.getItem().getOwner().getId().equals(userId)
                && !booking.getBooker().getId().equals(userId)) {
            throw new ValidationException("Пользователь должен являться хозяином вещи или автором бронирования");
        }
        return bookingMapper.convertToBookingWithUserAndItemDto(booking);
    }

    @Override
    public List<BookingWithUserAndItemDto> getAllByUser(Long userId, String state) {
        getUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBookerId(userId, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный статус: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::convertToBookingWithUserAndItemDto)
                .toList();
    }

    public List<BookingWithUserAndItemDto> getAllByOwner(Long ownerId, String state) {
        getUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный статус: " + state);
        }

        return bookings.stream()
                .map(bookingMapper::convertToBookingWithUserAndItemDto)
                .toList();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }
}
