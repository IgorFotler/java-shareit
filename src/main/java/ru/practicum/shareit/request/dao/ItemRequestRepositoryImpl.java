package ru.practicum.shareit.request.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.*;

@Slf4j
@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {

    private Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        itemRequest.setId(idCounter++);
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public List<ItemRequest> getAll() {
        return new ArrayList<>(itemRequests.values());
    }

    @Override
    public ItemRequest getById(Long id) {
        return itemRequests.values()
                .stream()
                .filter(itemRequest -> Objects.equals(itemRequest.getId(), id))
                .findFirst()
                .orElseThrow(() -> {
                    String errorMessage = String.format("Запрос с id %d не найден", id);
                    log.error(errorMessage);
                    throw new ItemRequestNotFoundException(errorMessage);
                });
    }
}
