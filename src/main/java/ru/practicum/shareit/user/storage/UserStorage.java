package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User update(User user, long id);

    User getById(long id);

    void delete(long id);

    Collection<User> getAll();
}
