package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Поле description не может быть пустым")
    private String description;

    @NotNull(message = "Поле requestorId не может быть пустым")
    private Long requestorId;

    List<ItemDto> items;

    @NotNull(message = "Поле created не может быть пустым")
    private LocalDateTime created;
}
