package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userIdHeader = "X-Sharer-User-Id";

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        ItemDto inputItem = new ItemDto(null, "item1", "desc", true, null);
        ItemDto returnedItem = new ItemDto(1L, "item1", "desc", true, null);

        Mockito.when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(returnedItem);

        mockMvc.perform(post("/items")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item1"));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto inputItem = new ItemDto(null, "item-updated", "desc2", true, null);
        ItemDto returnedItem = new ItemDto(1L, "item-updated", "desc2", true, null);

        Mockito.when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(returnedItem);

        mockMvc.perform(patch("/items/1")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item-updated"));
    }

    @Test
    void getItemById_ShouldReturnItemWithBooking() throws Exception {
        ItemWithBookingDto returnedItem = new ItemWithBookingDto(
                1L, "item1", "desc", true, null, null, null, List.of()
        );

        Mockito.when(itemService.getById(1L, 1L)).thenReturn(returnedItem);

        mockMvc.perform(get("/items/1")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item1"));
    }

    @Test
    void getAllByOwner_ShouldReturnListOfItems() throws Exception {
        ItemWithBookingDto item = new ItemWithBookingDto(
                1L, "item1", "desc", true, null, null, null, List.of()
        );

        Mockito.when(itemService.getAllByOwner(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("item1"));
    }

    @Test
    void search_ShouldReturnFoundItems() throws Exception {
        ItemDto foundItem = new ItemDto(1L, "item", "desc", true, null);

        Mockito.when(itemService.search("item")).thenReturn(List.of(foundItem));

        mockMvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("item"));
    }

    @Test
    void addComment_ShouldReturnSavedComment() throws Exception {
        CommentDto inputComment = new CommentDto(null, "Great!", "Author", null);
        CommentDto savedComment = new CommentDto(1L, "Great!", "Author", LocalDateTime.now());

        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class)))
                .thenReturn(savedComment);

        mockMvc.perform(post("/items/1/comment")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great!"))
                .andExpect(jsonPath("$.authorName").value("Author"));
    }
}