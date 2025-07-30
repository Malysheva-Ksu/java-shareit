package ru.practicum.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserUpdateDto;

public interface UserService {
    Page<UserResponseDto> getAllUsers(Pageable pageable);

    UserResponseDto getUserById(Long userId);

    UserResponseDto createUser(UserCreateDto userDto);

    UserResponseDto updateUser(Long userId, UserUpdateDto userDto);

    void deleteUser(Long userId);
}