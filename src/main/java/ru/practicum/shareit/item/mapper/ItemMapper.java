package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemMapper {

    public ItemDto convertToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), item.getItemRequest() != null ? item.getItemRequest().getId() : null);
    }

    public Item convertToItem(ItemDto itemDto, Long ownerId, ItemRequest itemRequest) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), ownerId, itemRequest);
    }
}
