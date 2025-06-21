package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotBeOwnerException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        userRepository.getById(ownerId);
        ItemRequest itemRequest = null;
        if (itemDto.getItemRequestId() != null) {
            itemRequest = itemRequestRepository.getById(itemDto.getItemRequestId());
        }

        Item item = itemMapper.convertToItem(itemDto, ownerId, itemRequest);
        itemRepository.create(ownerId, item);
        return itemMapper.convertToItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.getById(userId);
        Item item = itemRepository.getById(itemId);

        if (!userId.equals(item.getOwnerId())) {
            throw new NotBeOwnerException("Вещь может редактировать только её владелец");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.convertToItemDto(item);
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.getById(id);
        return itemMapper.convertToItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return itemRepository.getAllByOwner(ownerId).stream()
                .map(itemMapper::convertToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::convertToItemDto)
                .toList();
    }
}
