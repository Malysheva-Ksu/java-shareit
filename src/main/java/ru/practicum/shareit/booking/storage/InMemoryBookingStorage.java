package ru.practicum.shareit.booking.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingStorage implements BookingStorage {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(currentId.incrementAndGet());
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    @Override
    public List<Booking> findAllByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(b -> b.getBooker().getId().equals(bookerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findAllByItemOwnerId(Long ownerId) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getOwner().getId().equals(ownerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isItemUnavailable(Long itemId, LocalDateTime start, LocalDateTime end) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .anyMatch(b -> (start.isBefore(b.getEnd()) && end.isAfter(b.getStart())));
    }

    @Override
    public List<Booking> findAllByItemId(List<Long> itemIds) {
        return bookings.values().stream()
                .filter(booking -> itemIds.contains(booking.getItem().getId()))
                .collect(Collectors.toList());
    }
}