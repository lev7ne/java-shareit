package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto add(UserDto userDto);
    UserDto update(UserDto userDto, long id);
    UserDto getById(long id);
    void delete(long id);
    Collection<UserDto> getAll();
}
