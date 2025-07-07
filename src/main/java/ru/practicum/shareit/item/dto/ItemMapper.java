package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.dto.NearestBookingDto;

import java.util.List;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ItemResponseDto toItemResponseDto(Item item, NearestBookingDto lastBooking, NearestBookingDto nextBooking, List<CommentResponseDto> comments) {
        if (item == null) {
            return null;
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static Item toItem(ItemCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        return Item.builder()
                .name(createDto.getName())
                .description(createDto.getDescription())
                .available(createDto.getAvailable())
                .build();
    }
}