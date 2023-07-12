package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Counter;
import ru.practicum.shareit.util.exception.EmptyEmailException;
import ru.practicum.shareit.util.exception.DuplicateEmailException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> userMap = new HashMap<>();
    Counter counter = new Counter();

    @Override
    public User add(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new EmptyEmailException("Поле email пустое.");
        }
        validateEmail(user.getEmail());
        user.setId(counter.createId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, long id) {
        if (!userMap.containsKey(id)) {
            throw new NotFoundException("Попытка обновить пользователя с несуществующим id: " + id);
        }
        User anyUser = getById(id);

        if (user.getName() != null && !user.getName().isBlank()) {
            anyUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.getEmail().equals(anyUser.getEmail())) {
                validateEmail(user.getEmail());
            }
            anyUser.setEmail(user.getEmail());
        }
        return userMap.put(anyUser.getId(), anyUser);
    }

    @Override
    public User getById(long id) {
        return userMap.get(id);
    }

    @Override
    public void delete(long id) {
        userMap.remove(id);
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    private void validateEmail(String email) {
        Collection<String> emails = getAll().stream()
                .map(User::getEmail)
                .filter(userEmail -> userEmail.equals(email))
                .collect(Collectors.toList());
        if (!emails.isEmpty()) {
            throw new DuplicateEmailException("Email: " + email + " - уже существует!");
        }

    }
}
