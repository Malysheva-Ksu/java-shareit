package ru.practicum.item.dto;

import ru.practicum.item.Comment;
import ru.practicum.item.Item;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

public class CommentMapper {

    private CommentMapper() {
    }

    public static Comment toComment(CommentRequestDto requestDto, Item item, User author) {
        if (requestDto == null) {
            return null;
        }
        return Comment.builder()
                .text(requestDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        String authorName = Optional.ofNullable(comment.getAuthor())
                .map(User::getName)
                .orElse(null);

        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(authorName)
                .created(comment.getCreated())
                .build();
    }
}