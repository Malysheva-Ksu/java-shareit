package ru.practicum.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient requestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long requesterId,
                                         @Valid @RequestBody ItemRequestCreateDto createDto) {
        return requestClient.create(requesterId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> findOwn(@RequestHeader(USER_ID_HEADER) Long requesterId) {
        return requestClient.findOwn(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                          @Positive @RequestParam(defaultValue = "10") int size) {
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long requestId) {
        return requestClient.findById(userId, requestId);
    }
}