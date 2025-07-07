package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        List<ItemDto> itemDtos = request.getItems() == null ? Collections.emptyList() :
                request.getItems().stream()
                        .map(ItemRequestMapper::toItemDto)
                        .collect(Collectors.toList());

        return ItemRequestResponseDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requesterId(request.getRequester().getId())
                .createdAt(request.getCreatedAt())
                .items(itemDtos)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestCreateDto createDto, User requester) {
        if (createDto == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(createDto.getDescription())
                .requester(requester)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }
}