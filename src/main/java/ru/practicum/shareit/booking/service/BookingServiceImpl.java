package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long bookerId, BookingRequestDto bookingDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + bookerId + " не найден."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + bookingDto.getItemId() + " не найдена."));

        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Вещь недоступна для бронирования.");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingSelfOwnershipException("Владелец не может забронировать собственную вещь.");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new InvalidBookingTimeException("Некорректное время бронирования.");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID " + bookingId + " не найдено."));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new BookingPermissionException("Только владелец вещи может подтверждать бронирование.");
        }
        if (booking.getStatus() == BookingStatus.APPROVED && approved) {
            throw new BookingAlreadyApprovedException("Бронирование уже подтверждено.");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с ID " + bookingId + " не найдено."));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingPermissionException("Нет доступа к бронированию.");
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsForUser(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден."));
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> bookingsPage;

        switch (state) {
            case CURRENT:
                bookingsPage = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now, pageable);
                break;
            case PAST:
                bookingsPage = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
                break;
            case FUTURE:
                bookingsPage = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable);
                break;
            case WAITING:
                bookingsPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingsPage = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            case ALL:
            default:
                bookingsPage = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                break;
        }

        return bookingsPage.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsForOwner(Long ownerId, BookingState state, int from, int size) {
        userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + ownerId + " не найден."));
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> bookingsPage;

        switch (state) {
            case CURRENT:
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now, pageable);
                break;
            case PAST:
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable);
                break;
            case FUTURE:
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable);
                break;
            case WAITING:
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
                break;
            case ALL:
            default:
                bookingsPage = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                break;
        }

        return bookingsPage.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}