package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String name;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Формат email некорректен.")
    private String email;
}