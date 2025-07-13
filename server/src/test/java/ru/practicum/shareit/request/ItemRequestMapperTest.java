package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private ItemRequestMapper mapper;
    private User user;
    private ItemRequest itemRequest;
    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        mapper = new ItemRequestMapper();
        user = new User(1L, "User", "user@mail.com");
        created = LocalDateTime.now();
        itemRequest = new ItemRequest(10L, "Need bike", user, created);
    }

    @Test
    void convertToItemRequestDtoTest() {
        ItemDto itemDto = new ItemDto(1L, "Bike", "Mountain bike", true, 10L);
        List<ItemDto> items = List.of(itemDto);

        ItemRequestDto dto = mapper.convertToItemRequestDto(itemRequest, items);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(itemRequest.getId());
        assertThat(dto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(dto.getItems()).containsExactly(itemDto);
    }

    @Test
    void convertToItemRequestTest() {
        ItemRequestDto dto = new ItemRequestDto(15L, "Need tool", user.getId(), List.of(), created);

        ItemRequest entity = mapper.convertToItemRequest(dto, user);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getRequestor()).isEqualTo(user);
        assertThat(entity.getCreated()).isEqualTo(created);
    }
}