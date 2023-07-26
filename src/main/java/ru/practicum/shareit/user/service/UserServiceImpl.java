package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto save(UserDto userDto) {
        return UserDtoMapper.toUserDto(userRepository.save(UserDtoMapper.toUser(userDto)));
    }

    @Override
    public UserDto findById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден или ещё не создан.");
        }

        return UserDtoMapper.toUserDto(optionalUser.get());
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        UserDto updatedUserDto = findById(id);

        if (userDto.getName() != null) {
            updatedUserDto.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            updatedUserDto.setEmail(userDto.getEmail());
        }

        return UserDtoMapper.toUserDto(userRepository.save(UserDtoMapper.toUser(updatedUserDto)));
    }
}
