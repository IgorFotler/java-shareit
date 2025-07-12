package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(userIdHeader) Long userId, @RequestBody ItemRequestForCreateDto request) {
        log.info("Получен HTTP-запрос на создание запроса: {}", request);
        ItemRequestDto createdRequest = itemRequestService.createRequest(userId, request);
        log.info("Успешно обработан HTTP-запрос на создание запроса: {}", request);
        return createdRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(userIdHeader) Long userId) {
        log.info("Получен HTTP-запрос на получение списка запросов пользователя с id: {}", userId);
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        log.info("Успешно обработан HTTP-запрос на получение списка запросов пользователя с id: {}", userId);
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(userIdHeader) Long userId) {
        log.info("Получен HTTP-запрос на получение списка всех запросов");
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId);
        log.info("Успешно обработан HTTP-запрос на получение списка всех запросов");
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(userIdHeader) Long userId, @PathVariable Long requestId) {
        log.info("Получен HTTP-запрос на получение запроса с id: {}", requestId);
        ItemRequestDto request = itemRequestService.getRequestById(userId, requestId);
        log.info("Успешно выполнен HTTP-запрос на получение запроса с id: {}", requestId);
        return request;
    }
}
