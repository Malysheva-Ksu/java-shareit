package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIn(List<Item> items);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}