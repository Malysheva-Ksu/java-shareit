package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.NearestBookingDto;
import java.util.List;

@Data
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private NearestBookingDto lastBooking;
    private NearestBookingDto nextBooking;
    private List<CommentResponseDto> comments;
}