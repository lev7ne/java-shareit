package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    UserDto findById(long id);

    void delete(long id);

    Collection<UserDto> getAll();
}
