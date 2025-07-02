package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage {
    Booking save(Booking booking);

    Optional<Booking> findById(Long bookingId);

    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllByItemOwnerId(Long ownerId);

    boolean isItemUnavailable(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemId(List<Long> itemIds);
}