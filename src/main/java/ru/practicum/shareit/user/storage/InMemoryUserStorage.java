package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Counter;
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
        validateEmail(user.getEmail());
        user.setId(counter.createId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, long id) {
        User updatedUser = getById(id);

        if (user.getEmail() != null && !user.getEmail().equals(updatedUser.getEmail())) {
            validateEmail(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        return userMap.put(updatedUser.getId(), updatedUser);
    }

    @Override
    public User getById(long id) {
        User user = userMap.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден или ещё не создан.");
        }
        return user;
    }

    @Override
    public void delete(long id) {
        getById(id);
        userMap.remove(id);
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    private void validateEmail(String email) {
        Collection<String> emails = getAll().stream().map(User::getEmail).filter(userEmail -> userEmail.equals(email)).collect(Collectors.toList());
        if (!emails.isEmpty()) {
            throw new DuplicateEmailException("Email: " + email + " уже используется другим пользователем.");
        }
    }
}