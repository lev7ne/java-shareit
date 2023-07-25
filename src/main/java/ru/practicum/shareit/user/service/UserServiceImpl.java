package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto add(UserDto userDto) {
        return UserDtoMapper.toUserDto(userStorage.add(UserDtoMapper.toUser(userDto)));
    }

    @Override
    public UserDto getById(long id) {
        return UserDtoMapper.toUserDto(userStorage.getById(id));
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        userStorage.delete(id);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        return UserDtoMapper.toUserDto(userStorage.update(UserDtoMapper.toUser(userDto), id));
    }
}
