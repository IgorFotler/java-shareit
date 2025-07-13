package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl service;

    @Mock private ItemRequestRepository itemRequestRepository;
    @Mock private ItemRequestMapper itemRequestMapper;
    @Mock private UserService userService;
    @Mock private UserMapper userMapper;
    @Mock private ItemService itemService;
    @Mock private UserRepository userRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRequestTest() {
        Long userId = 1L;
        ItemRequestForCreateDto requestDto = new ItemRequestForCreateDto("description");
        UserDto userDto = new UserDto(userId, "name", "email");
        User user = new User(userId, "name", "email");
        ItemRequest savedRequest = new ItemRequest(10L, "description", user, LocalDateTime.now());
        List<ItemDto> items = List.of();

        when(userService.getById(userId)).thenReturn(userDto);
        when(userMapper.convertToUser(userDto)).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);
        when(itemService.getItemsByRequestId(savedRequest.getId())).thenReturn(items);
        when(itemRequestMapper.convertToItemRequestDto(savedRequest, items)).thenReturn(new ItemRequestDto());

        ItemRequestDto result = service.createRequest(userId, requestDto);

        assertNotNull(result);
        verify(itemRequestRepository).save(any());
    }

    @Test
    void getUserRequestsTest() {
        Long userId = 1L;
        User user = new User(userId, "name", "email");
        ItemRequest request = new ItemRequest(1L, "desc", user, LocalDateTime.now());
        List<Item> items = List.of();
        List<ItemDto> itemDtos = List.of();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)).thenReturn(List.of(request));
        when(itemRepository.findByItemRequestIdIn(List.of(1L))).thenReturn(items);
        when(itemMapper.convertToItemDto(any())).thenReturn(new ItemDto());
        when(itemRequestMapper.convertToItemRequestDto(eq(request), any())).thenReturn(new ItemRequestDto());

        List<ItemRequestDto> result = service.getUserRequests(userId);

        assertEquals(1, result.size());
    }

    @Test
    void getUserRequestsUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUserRequests(1L));
    }

    @Test
    void getAllRequestsTest() {
        Long userId = 2L;
        User user = new User(userId, "another", "mail");
        ItemRequest request = new ItemRequest(2L, "other", user, LocalDateTime.now());
        List<Item> items = List.of();
        List<ItemDto> itemDtos = List.of();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId)).thenReturn(List.of(request));
        when(itemRepository.findByItemRequestIdIn(List.of(2L))).thenReturn(items);
        when(itemMapper.convertToItemDto(any())).thenReturn(new ItemDto());
        when(itemRequestMapper.convertToItemRequestDto(eq(request), any())).thenReturn(new ItemRequestDto());

        List<ItemRequestDto> result = service.getAllRequests(userId);

        assertEquals(1, result.size());
    }

    @Test
    void getRequestByIdTest() {
        Long userId = 1L;
        Long requestId = 5L;
        User user = new User(userId, "name", "email");
        ItemRequest request = new ItemRequest(requestId, "desc", user, LocalDateTime.now());
        List<ItemDto> items = List.of();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemService.getItemsByRequestId(requestId)).thenReturn(items);
        when(itemRequestMapper.convertToItemRequestDto(request, items)).thenReturn(new ItemRequestDto());

        ItemRequestDto result = service.getRequestById(userId, requestId);

        assertNotNull(result);
    }

    @Test
    void getRequestByIdNotFound() {
        Long userId = 1L;
        Long requestId = 10L;
        User user = new User(userId, "u", "m");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getRequestById(userId, requestId));
    }

    @Test
    void getRequestByIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getRequestById(1L, 2L));
    }
}