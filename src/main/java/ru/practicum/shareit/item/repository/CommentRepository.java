package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);

    @Query("SELECT c FROM Comment c JOIN FETCH c.item WHERE c.item.id IN :itemIds")
    List<Comment> findAllByItemIdInWithItem(List<Long> itemIds);
}