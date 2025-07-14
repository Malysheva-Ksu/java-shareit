package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ItemRequestNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemMapper;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.ItemRequestRepository;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestMapper;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long requesterId, ItemRequestCreateDto createDto) {
        User requester = findUserOrThrow(requesterId);
        ItemRequest request = ItemRequestMapper.toItemRequest(createDto, requester);
        request = requestRepository.save(request);
        return ItemRequestMapper.toResponseDto(request, Collections.emptyList());
    }

    @Override
    public List<ItemRequestResponseDto> findOwn(Long requesterId, int from, int size) {
        findUserOrThrow(requesterId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Page<ItemRequest> requestsPage = requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId, pageable);

        return enrichRequestsWithItems(requestsPage.getContent());
    }

    @Override
    public List<ItemRequestResponseDto> findAll(Long userId, int from, int size) {
        findUserOrThrow(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        Page<ItemRequest> requestsPage = requestRepository.findByRequesterIdNot(userId, pageable);

        return enrichRequestsWithItems(requestsPage.getContent());
    }

    @Override
    public ItemRequestResponseDto findById(Long userId, Long requestId) {
        findUserOrThrow(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с ID " + requestId + " не найден."));

        List<ItemDto> items = itemRepository.findAllByRequestId(request.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toResponseDto(request, items);
    }

    private List<ItemRequestResponseDto> enrichRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(request -> ItemRequestMapper.toResponseDto(
                        request,
                        itemsByRequest.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден."));
    }
}