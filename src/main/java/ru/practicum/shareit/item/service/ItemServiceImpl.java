package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserAccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + ownerId + " не найден."));

        Item item = ItemMapper.toItem(itemDto, owner);

        if (itemDto.getRequestId() != null && itemRequestStorage != null) {
             ItemRequest request = itemRequestStorage.findById(itemDto.getRequestId()).orElse(null);
             item.setRequest(request);
         }

        Item savedItem = itemStorage.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + ownerId + " не найден."));

        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + itemId + " не найдена."));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new UserAccessDeniedException("Пользователь с ID " + ownerId + " не является владельцем вещи с ID " + itemId + ".");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemStorage.update(existingItem)
                .orElseThrow(() -> new ItemNotFoundException("Не удалось обновить вещь с ID " + itemId));
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + itemId + " не найдена."));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + ownerId + " не найден."));

        return itemStorage.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchAvailableByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}