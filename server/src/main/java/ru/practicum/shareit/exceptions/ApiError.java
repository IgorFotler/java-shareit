package ru.practicum.shareit.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    String description;
    Integer errorCode;
    String error;
}