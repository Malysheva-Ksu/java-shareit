package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;

}