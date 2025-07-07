package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemResponseDto> addItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                   @Valid @RequestBody ItemCreateDto createDto) {
        ItemResponseDto createdItem = itemService.addItem(ownerId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                      @PathVariable Long itemId,
                                                      @Valid @RequestBody ItemUpdateDto updateDto) {
        ItemResponseDto updatedItem = itemService.updateItem(ownerId, itemId, updateDto);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                       @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItemsByOwner(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(itemService.getItemsByOwner(ownerId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchAvailableItems(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items/search с текстом '{}'", text);
        return ResponseEntity.ok(itemService.searchAvailableItems(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader(USER_ID_HEADER) Long authorId,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentRequestDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(authorId, itemId, commentDto));
    }
}