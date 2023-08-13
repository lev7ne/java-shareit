package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.validator.ObjectHelper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        return UserDtoMapper.toUserDto(userRepository.save(UserDtoMapper.toUser(userDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(long id) {
        return UserDtoMapper.toUserDto(ObjectHelper.findUserById(userRepository, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long id) {
        ObjectHelper.findUserById(userRepository, id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto userDto) {
        User user = ObjectHelper.findUserById(userRepository, id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return UserDtoMapper.toUserDto(userRepository.save(user));
    }
}
