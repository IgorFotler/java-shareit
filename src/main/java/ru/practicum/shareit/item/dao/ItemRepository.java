package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long ownerId, Item item);

    Item getById(Long id);

    List<Item> getAllByOwner(Long ownerId);

    List<Item> search(String text);
}
