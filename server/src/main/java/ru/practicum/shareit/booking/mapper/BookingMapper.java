package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithUserAndItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public BookingDto convertToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem().getId(), booking.getBooker().getId(), booking.getStatus());
    }

    public Booking convertToBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), item, booker, bookingDto.getStatus());
    }

    public BookingWithUserAndItemDto convertToBookingWithUserAndItemDto(Booking booking) {
        return new BookingWithUserAndItemDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }
}
