package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Поле start не может быть пустым")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Поле end не может быть пустым")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future
    private LocalDateTime end;

    @NotNull(message = "Поле itemId не может быть пустым")
    @Positive
    private Long itemId;

    @NotNull(message = "Поле bookerId не может быть пустым")
    @Positive
    private Long bookerId;
    private BookingStatus status = BookingStatus.WAITING;
}
