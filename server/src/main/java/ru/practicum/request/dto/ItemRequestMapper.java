package ru.practicum.request.dto;

import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.Item;
import ru.practicum.request.ItemRequest;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto createDto, User requester) {
        if (createDto == null || requester == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(createDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestResponseDto toResponseDto(ItemRequest request, List<ItemDto> items) {
        if (request == null) {
            return null;
        }

        Long requesterId = (request.getRequester() != null) ? request.getRequester().getId() : null;

        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requesterId(requesterId)
                .created(request.getCreated())
                .items(items != null ? items : Collections.emptyList())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        Long requestId = (item.getRequest() != null) ? item.getRequest().getId() : null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }
}