package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    private CommentMapper() {
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .itemId(comment.getItem() != null ? comment.getItem().getId() : null)
                .build();
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        if (commentDto == null) {
            return null;
        }
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(java.time.LocalDateTime.now())
                .build();
    }
}