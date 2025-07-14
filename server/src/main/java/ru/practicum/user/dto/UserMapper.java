package ru.practicum.user.dto;

import ru.practicum.user.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponseDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserResponseDto toUserResponseDto(User user) {
        if (user == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        return User.builder()
                .name(createDto.getName())
                .email(createDto.getEmail())
                .build();
    }
}