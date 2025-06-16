package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.List;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        List<ItemDto> itemDtos = Collections.emptyList();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            itemDtos = request.getItems().stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(UserMapper.toUserDto(request.getRequester()))
                .created(request.getCreated())
                .items(itemDtos)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requesterEntity) {
        if (requestDto == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requester(requesterEntity)
                .created(requestDto.getCreated() != null ? requestDto.getCreated() : java.time.LocalDateTime.now())
                .build();
    }
}