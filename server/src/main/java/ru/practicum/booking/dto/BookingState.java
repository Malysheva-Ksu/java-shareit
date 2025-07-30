package ru.practicum.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> from(String text) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(text)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}