package ru.practicum.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}