package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForCreateDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestForCreateDto request) {
        User requestor = userMapper.convertToUser(userService.getById(userId));
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.convertToItemRequestDto(savedItemRequest, itemService.getItemsByRequestId(savedItemRequest.getId()));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
    getUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findByItemRequestIdIn(requestIds);
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::convertToItemDto)
                .toList();

        Map<Long, List<ItemDto>> itemsByRequestId = itemsDto.stream()
                .collect(Collectors.groupingBy(itemDto -> itemDto.getRequestId()));

        return requests.stream()
                .map(request -> itemRequestMapper.convertToItemRequestDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        getUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findByItemRequestIdIn(requestIds);
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::convertToItemDto)
                .toList();

        Map<Long, List<ItemDto>> itemsByRequestId = itemsDto.stream()
                .collect(Collectors.groupingBy(itemDto -> itemDto.getRequestId()));

        return requests.stream()
                .map(request -> itemRequestMapper.convertToItemRequestDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        return itemRequestMapper.convertToItemRequestDto(itemRequest, itemService.getItemsByRequestId(itemRequest.getId()));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
    }
}

