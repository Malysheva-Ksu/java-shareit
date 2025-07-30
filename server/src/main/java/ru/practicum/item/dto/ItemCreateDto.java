package ru.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Название не может быть пустым.")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(max = 1000)
    private String description;

    @NotNull(message = "Статус доступности должен быть указан.")
    private Boolean available;

    private Long requestId;
}