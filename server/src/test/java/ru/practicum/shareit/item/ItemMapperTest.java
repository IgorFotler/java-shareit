package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private ItemMapper itemMapper;
    private User owner;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
        owner = new User(1L, "Owner", "owner@mail.com");
        request = new ItemRequest(10L, "Need drill", owner, null);
        item = new Item(2L, "Drill", "Powerful drill", true, owner, request);
    }

    @Test
    void convertToItemDto_shouldMapCorrectly() {
        ItemDto dto = itemMapper.convertToItemDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void convertToItem_shouldMapCorrectly() {
        ItemDto dto = new ItemDto(3L, "Hammer", "Steel hammer", false, request.getId());

        Item entity = itemMapper.convertToItem(dto, owner, request);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getOwner()).isEqualTo(owner);
        assertThat(entity.getItemRequest()).isEqualTo(request);
    }

    @Test
    void convertToItemWithBookingDto_shouldMapCorrectly() {
        BookingDto last = new BookingDto(1L, null, null, 2L, 3L, null);
        BookingDto next = new BookingDto(2L, null, null, 2L, 4L, null);
        CommentDto comment = new CommentDto(5L, "Nice", "User", null);

        ItemWithBookingDto dto = itemMapper.convertToItemWithBookingDto(item, last, next, List.of(comment));

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getLastBooking()).isEqualTo(last);
        assertThat(dto.getNextBooking()).isEqualTo(next);
        assertThat(dto.getComments()).hasSize(1).contains(comment);
    }
}