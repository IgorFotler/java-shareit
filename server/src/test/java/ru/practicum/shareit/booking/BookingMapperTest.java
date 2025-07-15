package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserAndItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private BookingMapper bookingMapper;
    private Booking booking;
    private Item item;
    private User booker;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapper();
        item = new Item(1L, "Drill", "Electric drill", true, new User(2L, "Owner", "owner@mail.com"), null);
        booker = new User(3L, "Booker", "booker@mail.com");
        booking = new Booking(10L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, booker, BookingStatus.WAITING);
    }

    @Test
    void convertToBookingDtoTest() {
        BookingDto dto = bookingMapper.convertToBookingDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getItemId()).isEqualTo(item.getId());
        assertThat(dto.getBookerId()).isEqualTo(booker.getId());
        assertThat(dto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void convertToBookingTest() {
        BookingDto dto = new BookingDto(5L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), item.getId(), booker.getId(), BookingStatus.APPROVED);

        Booking entity = bookingMapper.convertToBooking(dto, item, booker);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getItem()).isEqualTo(item);
        assertThat(entity.getBooker()).isEqualTo(booker);
        assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
    }

    @Test
    void convertToBookingWithUserAndItemDtoTest() {
        BookingWithUserAndItemDto dto = bookingMapper.convertToBookingWithUserAndItemDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(booking.getId());
        assertThat(dto.getItem()).isEqualTo(item);
        assertThat(dto.getBooker()).isEqualTo(booker);
        assertThat(dto.getStatus()).isEqualTo(booking.getStatus());
    }
}