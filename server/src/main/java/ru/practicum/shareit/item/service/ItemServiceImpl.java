package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotBeOwnerException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        User owner = getUserById(ownerId);

        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.getById(itemDto.getRequestId());
        }

        Item item = itemMapper.convertToItem(itemDto, owner, itemRequest);
        itemRepository.save(item);
        return itemMapper.convertToItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        getUserById(userId);
        Item item = getItemById(itemId);

        if (!userId.equals(item.getOwner().getId())) {
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

        itemRepository.save(item);
        return itemMapper.convertToItemDto(item);
    }

    @Override
    public ItemWithBookingDto getById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findTopByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
            nextBooking = bookingRepository.findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        }

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::convertToCommentDto)
                .toList();
        BookingDto lastBookingDto = lastBooking != null ? bookingMapper.convertToBookingDto(lastBooking) : null;
        BookingDto nextBookingDto = nextBooking != null ? bookingMapper.convertToBookingDto(nextBooking) : null;
        return itemMapper.convertToItemWithBookingDto(item, lastBookingDto,
                nextBookingDto, comments);
    }

    @Override
    public List<ItemWithBookingDto> getAllByOwner(Long ownerId) {
        List<Item> items = itemRepository.getAllByOwnerId(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<Booking>> bookingsMap = bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsMap = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {
                    Booking lastBooking = null;
                    Booking nextBooking = null;

                    List<Booking> itemBookings = bookingsMap.getOrDefault(item.getId(), Collections.emptyList());
                    if (!itemBookings.isEmpty()) {
                        lastBooking = itemBookings.stream()
                                .filter(b -> b.getStart().isBefore(now))
                                .max(Comparator.comparing(Booking::getEnd))
                                .orElse(null);

                        nextBooking = itemBookings.stream()
                                .filter(b -> b.getStart().isAfter(now))
                                .min(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                    }

                    List<CommentDto> comments = commentsMap.getOrDefault(item.getId(), Collections.emptyList())
                            .stream()
                            .map(commentMapper::convertToCommentDto)
                            .toList();

                    return itemMapper.convertToItemWithBookingDto(
                            item,
                            lastBooking != null ? bookingMapper.convertToBookingDto(lastBooking) : null,
                            nextBooking != null ? bookingMapper.convertToBookingDto(nextBooking) : null,
                            comments
                    );
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findByAvailableIsTrueAndNameContainingIgnoreCaseOrAvailableIsTrueAndDescriptionContainingIgnoreCase(text, text).stream()
                .map(itemMapper::convertToItemDto)
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = getUserById(userId);

        Item item = getItemById(itemId);

        boolean hasBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasBooking) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или арендане завершена");
        }

        Comment comment = commentMapper.convertToComment(commentDto, item, author);
        commentRepository.save(comment);
        return commentMapper.convertToCommentDto(comment);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        List<Item> items = itemRepository.findByItemRequestId(requestId);
        return items.stream()
                .map(itemMapper::convertToItemDto)
                .toList();
    }
}
