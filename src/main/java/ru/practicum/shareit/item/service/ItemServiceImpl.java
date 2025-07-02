package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NearestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserAccessDeniedException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = findUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElse(null);
            item.setRequest(request);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        findUserById(ownerId);

        Item existingItem = findItemById(itemId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new UserAccessDeniedException("Пользователь с ID " + ownerId + " не является владельцем вещи с ID " + itemId + ".");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(name -> {
            if (!name.isBlank()) existingItem.setName(name);
        });
        Optional.ofNullable(itemDto.getDescription()).ifPresent(desc -> {
            if (!desc.isBlank()) existingItem.setDescription(desc);
        });
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(existingItem::setAvailable);

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);

        enrichWithComments(List.of(itemDto));

        if (item.getOwner().getId().equals(userId)) {
            enrichWithBookings(List.of(itemDto));
        }

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new UserNotFoundException("Пользователь с ID " + ownerId + " не найден.");
        }

        List<Item> ownerItems = itemRepository.findAllByOwner_IdOrderByIdAsc(ownerId);
        if (ownerItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemDto> itemDtos = ownerItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        enrichWithComments(itemDtos);
        enrichWithBookings(itemDtos);

        return itemDtos;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto commentDto) {
        User author = findUserById(authorId);
        Item item = findItemById(itemId);

        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                authorId, itemId, LocalDateTime.now(), BookingStatus.APPROVED
        );

        if (!hasCompletedBooking) {
            throw new IllegalArgumentException("Пользователь с ID " + authorId + " не может оставить отзыв на вещь с ID " +
                    itemId + ", так как не брал ее в аренду или аренда еще не завершилась.");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchAvailableItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден."));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с ID " + itemId + " не найдена."));
    }

    private void enrichWithComments(List<ItemDto> itemDtos) {
        List<Long> itemIds = itemDtos.stream().map(ItemDto::getId).collect(Collectors.toList());
        Map<Long, List<CommentDto>> commentsByItemId = commentRepository.findAllByItemIdIn(itemIds).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        itemDtos.forEach(dto -> dto.setComments(commentsByItemId.getOrDefault(dto.getId(), Collections.emptyList())));
    }

    private void enrichWithBookings(List<ItemDto> itemDtos) {
        List<Long> itemIds = itemDtos.stream().map(ItemDto::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<Booking>> bookingsByItemId = bookingRepository.findAllByItem_IdInAndStatus(itemIds, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        for (ItemDto dto : itemDtos) {
            List<Booking> itemBookings = bookingsByItemId.getOrDefault(dto.getId(), Collections.emptyList());

            itemBookings.stream()
                    .filter(b -> b.getStart().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .ifPresent(b -> dto.setLastBooking(new NearestBookingDto(b.getId(), b.getBooker().getId())));

            itemBookings.stream()
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .ifPresent(b -> dto.setNextBooking(new NearestBookingDto(b.getId(), b.getBooker().getId())));
        }
    }
}