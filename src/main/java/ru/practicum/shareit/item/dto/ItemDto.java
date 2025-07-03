package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.NearestBookingDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым.")
    @Size(max = 255, message = "Длина названия не должна превышать 255 символов.")
    private String name;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(max = 1000, message = "Длина описания не должна превышать 1000 символов.")
    private String description;

    private Boolean available;
    private Long requestId;
    private NearestBookingDto lastBooking;
    private NearestBookingDto nextBooking;
    private List<CommentDto> comments;
}