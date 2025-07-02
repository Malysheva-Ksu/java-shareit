package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден."));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userStorage.findByEmail(userDto.getEmail()).ifPresent(u -> {
            throw new EmailAlreadyExistsException("Email " + userDto.getEmail() + " уже используется.");
        });
        User user = UserMapper.toUser(userDto);
        User savedUser = userStorage.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден для обновления."));

        if (userDto.getEmail() != null && !userDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            userStorage.findByEmail(userDto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(userId)) {
                    throw new EmailAlreadyExistsException("Email " + userDto.getEmail() + " уже используется другим пользователем.");
                }
            });
        }

        User userToUpdate = UserMapper.toUser(userDto);
        userToUpdate.setId(userId);

        User updatedUser = userStorage.update(userToUpdate)
                .orElseThrow(() -> new UserNotFoundException("Не удалось обновить пользователя с ID " + userId));

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден для удаления.");
        }
        userStorage.deleteById(userId);
    }
}