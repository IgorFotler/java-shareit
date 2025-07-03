package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {

    public ItemRequestDto convertToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getRequestor(), itemRequest.getCreated());
    }

    public ItemRequest convertToItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), itemRequestDto.getRequestor(), itemRequestDto.getCreated());
    }
}
