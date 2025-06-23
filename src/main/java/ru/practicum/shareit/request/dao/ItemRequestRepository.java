package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


public interface ItemRequestRepository {
    ItemRequest create(ItemRequest itemRequest);

    List<ItemRequest> getAll();

    ItemRequest getById(Long id);
}
