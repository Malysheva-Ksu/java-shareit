package ru.practicum.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.dto.*;

import java.util.List;

public interface ItemService {

    @Transactional
    ItemResponseDto addItem(Long ownerId, ItemCreateDto createDto);

    @Transactional
    ItemResponseDto updateItem(Long ownerId, Long itemId, ItemUpdateDto updateDto);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getItemsByOwner(Long ownerId, int from, int size);

    @Transactional
    CommentResponseDto addComment(Long authorId, Long itemId, CommentRequestDto commentDto);

    List<ItemResponseDto> searchAvailableItems(String text, int from, int size);
}