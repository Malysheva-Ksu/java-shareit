package ru.practicum.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.item.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);

    @Query("SELECT c FROM Comment c JOIN FETCH c.item WHERE c.item.id IN :itemIds")
    List<Comment> findAllByItemIdInWithItem(@Param("itemIds") List<Long> itemIds);
}