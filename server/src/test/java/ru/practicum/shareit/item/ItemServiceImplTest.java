package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotBeOwnerException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock private ItemRepository itemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ItemRequestRepository itemRequestRepository;
    @Mock private ItemMapper itemMapper;
    @Mock private CommentRepository commentRepository;
    @Mock private CommentMapper commentMapper;
    @Mock private BookingRepository bookingRepository;
    @Mock private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateItemSuccess() {
        Long userId = 1L;
        Long itemId = 2L;
        User owner = new User(userId, "user", "email");
        ItemDto updateDto = new ItemDto(null, "updated", null, null, null);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        item.setName("old");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.convertToItemDto(item)).thenReturn(new ItemDto(itemId, "updated", null, null, null));

        ItemDto result = itemService.update(userId, itemId, updateDto);

        assertEquals("updated", result.getName());
        verify(itemRepository).save(item);
    }

    @Test
    void updateItemNotOwner() {
        Long ownerId = 1L;
        Long notOwnerId = 2L;
        Long itemId = 3L;
        User owner = new User(ownerId, "owner", "mail");
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(userRepository.findById(notOwnerId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotBeOwnerException.class,
                () -> itemService.update(notOwnerId, itemId, new ItemDto()));
    }

    @Test
    void getByIdReturnsWithBookingsAndComments() {
        Long userId = 1L;
        Long itemId = 10L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(new User(userId, "name", "email"));

        Booking lastBooking = mock(Booking.class);
        Booking nextBooking = mock(Booking.class);
        Comment comment = mock(Comment.class);
        CommentDto commentDto = new CommentDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findTopByItemIdAndStartBeforeAndStatusOrderByEndDesc(eq(itemId), any(), eq(BookingStatus.APPROVED)))
                .thenReturn(lastBooking);
        when(bookingRepository.findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(eq(itemId), any(), eq(BookingStatus.APPROVED)))
                .thenReturn(nextBooking);
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        when(commentMapper.convertToCommentDto(comment)).thenReturn(commentDto);
        when(bookingMapper.convertToBookingDto(any())).thenReturn(mock(ru.practicum.shareit.booking.dto.BookingDto.class));
        when(itemMapper.convertToItemWithBookingDto(eq(item), any(), any(), any()))
                .thenReturn(mock(ItemWithBookingDto.class));

        ItemWithBookingDto result = itemService.getById(itemId, userId);

        assertNotNull(result);
    }

    @Test
    void searchReturnsMatchingItems() {
        String text = "test";
        Item item = new Item();
        when(itemRepository.findByAvailableIsTrueAndNameContainingIgnoreCaseOrAvailableIsTrueAndDescriptionContainingIgnoreCase(text, text))
                .thenReturn(List.of(item));
        when(itemMapper.convertToItemDto(item)).thenReturn(new ItemDto());

        List<ItemDto> result = itemService.search(text);

        assertEquals(1, result.size());
    }

    @Test
    void searchWithBlankReturnsEmpty() {
        List<ItemDto> result = itemService.search("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void addCommentSuccess() {
        Long userId = 1L;
        Long itemId = 2L;
        User user = new User(userId, "name", "mail");
        Item item = new Item();
        Comment comment = new Comment();
        CommentDto dto = new CommentDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any()))
                .thenReturn(true);
        when(commentMapper.convertToComment(dto, item, user)).thenReturn(comment);
        when(commentMapper.convertToCommentDto(comment)).thenReturn(dto);

        CommentDto result = itemService.addComment(userId, itemId, dto);

        assertEquals(dto, result);
        verify(commentRepository).save(comment);
    }

    @Test
    void addCommentWithoutBookingThrows() {
        Long userId = 1L;
        Long itemId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(eq(userId), eq(itemId), eq(BookingStatus.APPROVED), any()))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.addComment(userId, itemId, new CommentDto()));
    }

    @Test
    void getItemByIdNotFoundShouldThrow() {
        User user = new User(1L, "user", "user@mail.com");
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ItemNotFoundException ex = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getById(999L, user.getId())
        );
        assertEquals("Вещь с id 999 не найдена", ex.getMessage());
    }

    @Test
    void getAllByOwnerSuccess() {
        User owner = new User(1L, "owner", "owner@mail.com");
        Item item = new Item(1L, "itemName", "desc", true, owner, null);
        Booking pastBooking = new Booking(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1), item, owner, BookingStatus.APPROVED);
        Booking futureBooking = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), item, owner, BookingStatus.APPROVED);
        Comment comment = new Comment(1L, "Nice item", item, owner, LocalDateTime.now().minusDays(2));

        BookingDto pastBookingDto = new BookingDto();
        BookingDto futureBookingDto = new BookingDto();
        CommentDto commentDto = new CommentDto();
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();

        when(itemRepository.getAllByOwnerId(owner.getId())).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdInAndStatus(List.of(item.getId()), BookingStatus.APPROVED)).thenReturn(List.of(pastBooking, futureBooking));
        when(commentRepository.findByItemIdIn(List.of(item.getId()))).thenReturn(List.of(comment));
        when(commentMapper.convertToCommentDto(comment)).thenReturn(commentDto);
        when(bookingMapper.convertToBookingDto(pastBooking)).thenReturn(pastBookingDto);
        when(bookingMapper.convertToBookingDto(futureBooking)).thenReturn(futureBookingDto);
        when(itemMapper.convertToItemWithBookingDto(eq(item), eq(pastBookingDto), eq(futureBookingDto), anyList()))
                .thenReturn(itemWithBookingDto);

        List<ItemWithBookingDto> result = itemService.getAllByOwner(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(itemWithBookingDto);
    }
}