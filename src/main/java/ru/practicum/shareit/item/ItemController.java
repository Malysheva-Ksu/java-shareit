package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                           @Valid @RequestBody ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            log.warn("Некорректные данные для создания вещи: {}", itemDto);
            return ResponseEntity.badRequest().build();
        }
        ItemDto createdItem = itemService.addItem(ownerId, itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(ownerId, itemId, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId,
                                               @RequestHeader(value = USER_ID_HEADER) Long userId) {
        ItemDto itemDto = itemService.getItemById(itemId, userId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        List<ItemDto> items = itemService.getItemsByOwner(ownerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text,
                                                     @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        List<ItemDto> foundItems = itemService.searchAvailableItems(text);
        return ResponseEntity.ok(foundItems);
    }
}