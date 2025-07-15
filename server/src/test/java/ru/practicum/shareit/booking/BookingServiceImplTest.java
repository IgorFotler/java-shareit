package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserAndItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingWithUserAndItemDto bookingWithUserAndItemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user", "user@mail.com");
        owner = new User(2L, "owner", "owner@mail.com");
        item = new Item(1L, "item", "desc", true, owner, null);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        booking = new Booking(1L, bookingDto.getStart(), bookingDto.getEnd(), item, user, BookingStatus.WAITING);

        bookingWithUserAndItemDto = new BookingWithUserAndItemDto();
        bookingWithUserAndItemDto.setId(1L);
    }

    @Test
    void createBookingSuccess() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingMapper.convertToBooking(bookingDto, item, user)).thenReturn(booking);
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking)).thenReturn(bookingWithUserAndItemDto);

        BookingWithUserAndItemDto result = bookingService.create(user.getId(), bookingDto);

        assertEquals(bookingWithUserAndItemDto.getId(), result.getId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBookingByOwnerShouldThrowValidationException() {
        item.setOwner(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto)
        );
        assertEquals("Вещь принадлежит данному пользователю", ex.getMessage());
    }

    @Test
    void updateBookingApprovedSuccess() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking)).thenReturn(bookingWithUserAndItemDto);

        BookingWithUserAndItemDto result = bookingService.update(owner.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingByNonOwnerShouldThrow() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.update(user.getId(), booking.getId(), true)
        );
        assertEquals("Подтвердить бронирование может только владелец", ex.getMessage());
    }

    @Test
    void getByIdForBookerSuccess() {
        booking.setBooker(user);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking)).thenReturn(bookingWithUserAndItemDto);

        BookingWithUserAndItemDto result = bookingService.getById(booking.getId(), user.getId());

        assertEquals(bookingWithUserAndItemDto.getId(), result.getId());
    }

    @Test
    void getByIdNotOwnerOrBookerShouldThrow() {
        booking.setBooker(new User(100L, "other", "other@mail.com"));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.getById(booking.getId(), user.getId())
        );
        assertEquals("Пользователь должен являться хозяином вещи или автором бронирования", ex.getMessage());
    }

    @Test
    void getAllByUserWithStateAll() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(user.getId()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking)).thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByUser(user.getId(), "ALL");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByUserWithStateWaiting() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByUser(user.getId(), "WAITING");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByUserWithStateCurrent() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentByBookerId(eq(user.getId()), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByUser(user.getId(), "CURRENT");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByUserWithStatePast() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByUser(user.getId(), "PAST");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByUserWithStateFuture() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(eq(user.getId()), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByUser(user.getId(), "FUTURE");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByUserWithStateRejected() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByUser(user.getId(), "REJECTED");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByUserWithUnknownStateShouldThrow() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getAllByUser(user.getId(), "UNKNOWN")
        );
        assertEquals("Неизвестный статус: UNKNOWN", ex.getMessage());
    }

    @Test
    void getAllByOwnerWithStateAll() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(owner.getId()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByOwner(owner.getId(), "ALL");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerWithStateCurrent() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findCurrentByOwnerId(eq(owner.getId()), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByOwner(owner.getId(), "CURRENT");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerWithStatePast() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(owner.getId()), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByOwner(owner.getId(), "PAST");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerWithStateFuture() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(owner.getId()), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByOwner(owner.getId(), "FUTURE");

        assertEquals(1, result.size());
    }

    @Test
    void getAllByOwnerWithStateRejected() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking))
                .thenReturn(bookingWithUserAndItemDto);

        List<BookingWithUserAndItemDto> result = bookingService.getAllByOwner(owner.getId(), "REJECTED");

        assertEquals(1, result.size());
    }

    @Test
    void createBookingWithUnavailableItemShouldThrowValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto)
        );
        assertEquals("Бронирование вещи недоступно", ex.getMessage());
    }

    @Test
    void updateAlreadyApprovedBookingShouldThrowValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), true)
        );
        assertEquals("Бронирование уже подтверждено", ex.getMessage());
    }

    @Test
    void updateCanceledBookingShouldThrowValidationException() {
        booking.setStatus(BookingStatus.CANCELED);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), true)
        );
        assertEquals("Бронирование отменено", ex.getMessage());
    }

    @Test
    void updateBookingRejected() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.convertToBookingWithUserAndItemDto(booking)).thenReturn(bookingWithUserAndItemDto);

        BookingWithUserAndItemDto result = bookingService.update(owner.getId(), booking.getId(), false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void getByIdWithNonexistentBookingShouldThrow() {
        User user = new User(1L, "user", "user@mail.com");
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        BookingNotFoundException ex = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.getById(999L, user.getId())
        );
        assertEquals("Бронирование с id 999 не найдено", ex.getMessage());
    }
}