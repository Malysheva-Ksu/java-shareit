package ru.practicum.exception;

public class BookingSelfOwnershipException extends RuntimeException {
    public BookingSelfOwnershipException(String message) {
        super(message);
    }
}