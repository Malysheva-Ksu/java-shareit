package ru.practicum.exception;

public class BookingPermissionException extends RuntimeException {
    public BookingPermissionException(String message) {
        super(message);
    }
}