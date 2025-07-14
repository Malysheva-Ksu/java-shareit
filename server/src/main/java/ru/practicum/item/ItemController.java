package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.*;
import ru.practicum.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @RequestBody ItemCreateDto createDto) {
        ItemResponseDto createdItem = itemService.addItem(ownerId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                      @PathVariable Long itemId,
                                                      @RequestBody ItemUpdateDto updateDto) {
        ItemResponseDto updatedItem = itemService.updateItem(ownerId, itemId, updateDto);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam int from,
            @RequestParam int size) {
        return ResponseEntity.ok(itemService.getItemsByOwner(ownerId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchAvailableItems(
            @RequestParam String text,
            @RequestParam int from,
            @RequestParam int size) {
        return ResponseEntity.ok(itemService.searchAvailableItems(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader("X-Sharer-User-Id") Long authorId,
            @PathVariable Long itemId,
            @RequestBody CommentRequestDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(authorId, itemId, commentDto));
    }
}