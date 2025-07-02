package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRequestStorage implements ItemRequestStorage {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            request.setId(currentId.incrementAndGet());
        }
        if (request.getCreated() == null) {
            request.setCreated(LocalDateTime.now());
        }
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<ItemRequest> findById(Long requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public List<ItemRequest> findAllByRequesterId(Long requesterId) {
        return requests.values().stream()
                .filter(request -> request.getRequester() != null && request.getRequester().getId().equals(requesterId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long requestId) {
        requests.remove(requestId);
    }
}