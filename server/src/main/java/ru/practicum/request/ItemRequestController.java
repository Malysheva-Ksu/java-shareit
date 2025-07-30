package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> create(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                                         @RequestBody ItemRequestCreateDto createDto) {
        return ResponseEntity.ok(requestService.create(requesterId, createDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> findOwn(
            @RequestHeader(USER_ID_HEADER) Long requesterId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(requestService.findOwn(requesterId, from, size));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                @RequestParam int from,
                                                                @RequestParam int size) {
        return ResponseEntity.ok(requestService.findAll(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                           @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.findById(userId, requestId));
    }
}