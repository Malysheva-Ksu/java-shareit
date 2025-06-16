package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestStorage {
    ItemRequest save(ItemRequest request);

    Optional<ItemRequest> findById(Long requestId);

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    void deleteById(Long requestId);
}