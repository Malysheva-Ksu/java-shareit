package ru.practicum.booking.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.item.dto.ItemResponseDto;
import ru.practicum.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long id;
    private BookingStatus status;
    private UserResponseDto booker;
    private ItemResponseDto item;

    private LocalDateTime start;
    private LocalDateTime end;

    private Long itemId;
}