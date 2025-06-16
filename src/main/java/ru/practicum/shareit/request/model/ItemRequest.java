package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;

    @Builder.Default
    private List<Item> items = new ArrayList<>();
}