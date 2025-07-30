package ru.practicum.booking.service;

import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long bookerId, BookingRequestDto bookingDto);

    BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingsForUser(Long userId, BookingState state, int from, int size);

    List<BookingResponseDto> getBookingsForOwner(Long ownerId, BookingState state, int from, int size);
}