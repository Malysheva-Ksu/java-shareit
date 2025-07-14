package ru.practicum.shareit.exception;

public class BookingSelfOwnershipException extends RuntimeException {
    public BookingSelfOwnershipException(String message) {
        super(message);
    }
}