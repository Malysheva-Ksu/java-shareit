package ru.practicum.exception;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException(String message) {
        super(message);
    }
}