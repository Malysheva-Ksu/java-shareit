package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserRepository userStorage;
    private final ItemRepository itemStorage;

    @Override
    public BookingResponseDto createBooking(Long bookerId, BookingRequestDto bookingDto) {
        User booker = userStorage.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + bookerId + " не найден."));

        Item item = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + bookingDto.getItemId() + " не найдена."));

        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Вещь с ID " + item.getId() + " недоступна для бронирования.");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingSelfOwnershipException("Владелец не может забронировать собственную вещь.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new InvalidBookingTimeException("Дата окончания бронирования должна быть после даты начала.");
        }
        if (bookingStorage.isItemUnavailable(item.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            throw new ItemUnavailableException("Вещь уже забронирована на указанные даты.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingStorage.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID " + bookingId + " не найдено."));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new BookingPermissionException("Только владелец вещи может подтверждать бронирование.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED && approved) {
            throw new BookingAlreadyApprovedException("Бронирование уже подтверждено.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingStorage.save(booking);
        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID " + bookingId + " не найдено."));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingPermissionException("Нет доступа к бронированию.");
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsForUser(Long userId, BookingState state, int from, int size) {
        userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден."));
        List<Booking> bookings = bookingStorage.findAllByBookerId(userId);
        return filterAndPaginateBookings(bookings, state, from, size);
    }

    @Override
    public List<BookingResponseDto> getBookingsForOwner(Long ownerId, BookingState state, int from, int size) {
        userStorage.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + ownerId + " не найден."));
        List<Booking> bookings = bookingStorage.findAllByItemOwnerId(ownerId);
        return filterAndPaginateBookings(bookings, state, from, size);
    }

    private List<BookingResponseDto> filterAndPaginateBookings(List<Booking> bookings, BookingState state, int from, int size) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> filteredBookings;
        switch (state) {
            case CURRENT:
                filteredBookings = bookings.stream().filter(b -> now.isAfter(b.getStart()) && now.isBefore(b.getEnd())).collect(Collectors.toList());
                break;
            case PAST:
                filteredBookings = bookings.stream().filter(b -> now.isAfter(b.getEnd())).collect(Collectors.toList());
                break;
            case FUTURE:
                filteredBookings = bookings.stream().filter(b -> now.isBefore(b.getStart())).collect(Collectors.toList());
                break;
            case WAITING:
                filteredBookings = bookings.stream().filter(b -> b.getStatus() == BookingStatus.WAITING).collect(Collectors.toList());
                break;
            case REJECTED:
                filteredBookings = bookings.stream().filter(b -> b.getStatus() == BookingStatus.REJECTED).collect(Collectors.toList());
                break;
            case ALL:
            default:
                filteredBookings = bookings;
                break;
        }

        return filteredBookings.stream()
                .skip(from)
                .limit(size)
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}