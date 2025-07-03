package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryCommentRepository implements CommentRepository {
    private final Map<Long, Comment> comments = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(currentId.incrementAndGet());
        }
        comments.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public List<Comment> findAllByItemId(Long itemId) {
        return comments.values().stream()
                .filter(comment -> comment.getItem() != null && comment.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findAllByItemIn(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        return comments.values().stream()
                .filter(comment -> comment.getItem() != null && itemIds.contains(comment.getItem().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findAllByItemIdIn(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.values().stream()
                .filter(comment -> comment.getItem() != null)
                .filter(comment -> itemIds.contains(comment.getItem().getId()))
                .collect(Collectors.toList());
    }
}