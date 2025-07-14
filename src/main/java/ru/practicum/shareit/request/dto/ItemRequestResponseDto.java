package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime createdAt;
    private List<ItemDto> items;
}