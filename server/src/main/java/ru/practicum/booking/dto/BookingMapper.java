package ru.practicum.booking.dto;

import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.Item;
import ru.practicum.user.User;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto.BookerDto bookerDto = BookingResponseDto.BookerDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();

        BookingResponseDto.ItemDto itemDto = BookingResponseDto.ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(bookerDto)
                .item(itemDto)
                .build();
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        if (bookingRequestDto == null) {
            return null;
        }

        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}