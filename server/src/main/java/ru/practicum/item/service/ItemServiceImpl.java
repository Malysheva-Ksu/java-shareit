package ru.practicum.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.NearestBookingDto;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.BookingRepository;
import ru.practicum.item.dto.*;
import ru.practicum.exception.*;
import ru.practicum.item.Comment;
import ru.practicum.item.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemResponseDto addItem(Long ownerId, ItemCreateDto createDto) {
        User owner = findUserById(ownerId);
        Item item = ItemMapper.toItem(createDto);
        item.setOwner(owner);

        if (createDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(createDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден"));
            item.setRequest(request);
        }

        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemResponseDto(savedItem, null, null, Collections.emptyList());
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long ownerId, Long itemId, ItemUpdateDto updateDto) {
        findUserById(ownerId);
        Item item = findItemById(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new UserAccessDeniedException("Пользователь не является владельцем вещи.");
        }

        Optional.ofNullable(updateDto.getName()).ifPresent(item::setName);
        Optional.ofNullable(updateDto.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(updateDto.getAvailable()).ifPresent(item::setAvailable);

        List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(toList());
        NearestBookingDto lastBooking = findLastBooking(itemId);
        NearestBookingDto nextBooking = findNextBooking(itemId);

        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);

        List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(toList());

        NearestBookingDto lastBooking = null;
        NearestBookingDto nextBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = findLastBooking(itemId);
            nextBooking = findNextBooking(itemId);
        }

        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getItemsByOwner(Long ownerId, int from, int size) {
        findUserById(ownerId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        List<Item> ownerItems = itemRepository.findAllByOwnerId(ownerId, pageable).getContent();

        if (ownerItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = ownerItems.stream().map(Item::getId).collect(toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<CommentResponseDto>> commentsMap = commentRepository.findAllByItemIdIn(itemIds).stream()
                .collect(groupingBy(comment -> comment.getItem().getId(),
                        mapping(CommentMapper::toCommentResponseDto, toList())));

        Map<Long, NearestBookingDto> lastBookingsMap = bookingRepository.findLastBookingsForItems(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        this::toNearestBookingDto,
                        (first, second) -> first
                ));

        Map<Long, NearestBookingDto> nextBookingsMap = bookingRepository.findNextBookingsForItems(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        this::toNearestBookingDto,
                        (first, second) -> first
                ));

        return ownerItems.stream()
                .map(item -> ItemMapper.toItemResponseDto(
                        item,
                        lastBookingsMap.get(item.getId()),
                        nextBookingsMap.get(item.getId()),
                        commentsMap.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long authorId, Long itemId, CommentRequestDto commentDto) {
        User author = findUserById(authorId);
        Item item = findItemById(itemId);

        boolean hasCompletedBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now()
        );

        if (!hasCompletedBooking) {
            throw new CommentValidationException("Пользователь не может оставить отзыв, т.к. не бронировал вещь.");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public List<ItemResponseDto> searchAvailableItems(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository.searchAvailableByText(text, pageable).stream()
                .map(item -> ItemMapper.toItemResponseDto(item, null, null, Collections.emptyList()))
                .collect(toList());
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден."));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена."));
    }

    private NearestBookingDto findLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(this::toNearestBookingDto).orElse(null);
    }

    private NearestBookingDto findNextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(this::toNearestBookingDto).orElse(null);
    }

    private NearestBookingDto toNearestBookingDto(Booking booking) {
        if (booking == null) return null;
        return new NearestBookingDto(booking.getId(), booking.getBooker().getId());
    }
}