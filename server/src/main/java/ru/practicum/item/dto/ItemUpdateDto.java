package ru.practicum.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    private Boolean available;
}