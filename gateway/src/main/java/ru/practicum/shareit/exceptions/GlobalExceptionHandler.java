package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCommentException(ValidationException ve) {
        return ApiError.builder().errorCode(HttpStatus.CONFLICT.value()).description(ve.getMessage()).build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUncaught(Exception exception) {
        return ApiError.builder().errorCode(HttpStatus.BAD_REQUEST.value()).description(exception.getMessage()).build();
    }
}