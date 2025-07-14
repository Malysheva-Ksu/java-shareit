package ru.practicum.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    @Transactional
    ItemRequestResponseDto create(Long requesterId, ItemRequestCreateDto createDto);

    List<ItemRequestResponseDto> findOwn(Long requesterId, int from, int size);

    List<ItemRequestResponseDto> findAll(Long userId, int from, int size);

    ItemRequestResponseDto findById(Long userId, Long requestId);
}