package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequestDto convertToItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequestor().getId(), items, itemRequest.getCreated());
    }

    public ItemRequest convertToItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), requestor, itemRequestDto.getCreated());
    }
}
