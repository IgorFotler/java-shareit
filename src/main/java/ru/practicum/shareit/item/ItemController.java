package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(userIdHeader) Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("Получен HTTP-запрос на создание вещи: {}", itemDto);
        ItemDto createdItem = itemService.create(userId, itemDto);
        log.info("Успешно обработан HTTP-запрос на создание вещи: {}", itemDto);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userIdHeader) Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Получен HTTP-запрос на обновление вещи: {}", itemDto);
        itemService.update(userId, itemId, itemDto);
        log.info("Успешно выполнен HTTP-запрос на обновление вещи: {}", itemDto);
        return itemDto;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable Long itemId) {
        log.info("Получен HTTP-запрос на получение вещи по id: {}", itemId);
        ItemDto itemDto = itemService.getById(itemId);
        log.debug("Найденная вещь: {}", itemDto);
        return itemDto;
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader(userIdHeader) Long ownerId) {
        log.info("Получен HTTP-запрос на получение вещей пользователя с id: {}", ownerId);
        List<ItemDto> allByOwner = itemService.getAllByOwner(ownerId);
        log.info("Успешно выполнен HTTP-запрос на получение вещей пользователя с id: {}", ownerId);
        return allByOwner;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Получен HTTP-запрос на поиск вещи: {}", text);
        List<ItemDto> searchResuls = itemService.search(text);
        log.info("Успешно выполнен HTTP-запрос на поиск вещи: {}", text);
        return searchResuls;
    }
}
