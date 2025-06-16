package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(currentId.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(User userToUpdate) {
        if (userToUpdate.getId() == null || !users.containsKey(userToUpdate.getId())) {
            return Optional.empty();
        }
        User existingUser = users.get(userToUpdate.getId());

        if (userToUpdate.getName() != null && !userToUpdate.getName().isBlank()) {
            existingUser.setName(userToUpdate.getName());
        }
        if (userToUpdate.getEmail() != null && !userToUpdate.getEmail().isBlank()) {
            String newEmail = userToUpdate.getEmail();
            if (!newEmail.equals(existingUser.getEmail()) && users.values().stream().anyMatch(u -> u.getEmail().equals(newEmail))) {
                throw new IllegalArgumentException("Email " + newEmail + " already exists.");
            }
            existingUser.setEmail(newEmail);
        }

        users.put(existingUser.getId(), existingUser);
        return Optional.of(existingUser);
    }

    @Override
    public void deleteById(Long userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}