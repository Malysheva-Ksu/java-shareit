package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> findById(Long userId);

    User save(User user);

    Optional<User> update(User user);

    void deleteById(Long userId);

    Optional<User> findByEmail(String email);
}