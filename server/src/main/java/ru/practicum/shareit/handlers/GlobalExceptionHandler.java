package ru.practicum.shareit.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.*;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleUserNotFoundException(UserNotFoundException unfe) {
        return ApiError.builder().errorCode(HttpStatus.NOT_FOUND.value()).description(unfe.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleBookingNotFoundException(BookingNotFoundException bnfe) {
        return ApiError.builder().errorCode(HttpStatus.NOT_FOUND.value()).description(bnfe.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleItemNotFoundException(ItemNotFoundException infe) {
        return ApiError.builder().errorCode(HttpStatus.NOT_FOUND.value()).description(infe.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException nfe) {
        return ApiError.builder().errorCode(HttpStatus.NOT_FOUND.value()).description(nfe.getMessage()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicationException(DuplicationException de) {
        return ApiError.builder().errorCode(HttpStatus.CONFLICT.value()).description(de.getMessage()).build();
    }

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