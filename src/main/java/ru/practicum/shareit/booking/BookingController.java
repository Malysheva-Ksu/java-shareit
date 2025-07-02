package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                            @RequestBody BookingRequestDto bookingDto) {
        BookingResponseDto createdBooking = bookingService.createBooking(bookerId, bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                             @PathVariable Long bookingId,
                                                             @RequestParam boolean approved) {
        BookingResponseDto updatedBooking = bookingService.approveBooking(ownerId, bookingId, approved);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                             @PathVariable Long bookingId) {
        BookingResponseDto booking = bookingService.getBookingById(userId, bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsForUser(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        List<BookingResponseDto> bookings = bookingService.getBookingsForUser(userId, bookingState, from, size);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getBookingsForOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        BookingState bookingState = BookingState.valueOf(state.toUpperCase());
        List<BookingResponseDto> bookings = bookingService.getBookingsForOwner(ownerId, bookingState, from, size);
        return ResponseEntity.ok(bookings);
    }
}