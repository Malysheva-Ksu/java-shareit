package ru.practicum.shareit.exception;

public class BookingPermissionException extends RuntimeException {
    public BookingPermissionException(String message) {
        super(message);
    }
}