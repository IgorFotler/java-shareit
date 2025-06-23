package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Item create(Long ownerId, Item item) {
        item.setId(idCounter++);
        item.setOwnerId(ownerId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(Long id) {
        Item item = items.get(id);
        if (item == null) {
            String errorMessage = String.format("Вещь с id %d не найдена", id);
            log.error(errorMessage);
            throw new ItemNotFoundException(errorMessage);
        }
        return item;
    }

    @Override
    public List<Item> getAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(i -> (i.getOwnerId().equals(ownerId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String textToSearch = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> (i.getName().toLowerCase().contains(textToSearch)
                        || i.getDescription().toLowerCase().contains(textToSearch)))
                .collect(Collectors.toList());
    }
}
