package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}