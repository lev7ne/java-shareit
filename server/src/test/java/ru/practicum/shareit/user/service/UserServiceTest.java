package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ObjectNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository mockUserRepository;
    private UserService userService;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(mockUserRepository);
        testUser1 = new User(1L, "name1", "email1@ya.ru");
        testUser2 = new User(2L, "name2", "email2@ya.ru");
    }

    @Test
    void find_whenValidId_thenReturnsUserDto() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        UserDto actualUser = userService.find(1L);

        assertEquals(1L, actualUser.getId());
        assertEquals(testUser1.getName(), actualUser.getName());
        assertEquals(testUser1.getEmail(), actualUser.getEmail());

        verify(mockUserRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void find_whenInvalidId_thenThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> userService.find(1L));

        verify(mockUserRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void save_whenValidUser_thenUserSaveInDb() {
        when(mockUserRepository.save(testUser1)).thenReturn(testUser1);

        UserDto actualUser = userService.save(UserDtoMapper.toUserDto(testUser1));

        assertEquals(1L, actualUser.getId());
        assertEquals(testUser1.getName(), actualUser.getName());
        assertEquals(testUser1.getEmail(), actualUser.getEmail());

        verify(mockUserRepository, Mockito.times(1)).save(testUser1);
    }

    @Test
    @Transactional
    void save_whenInvalidUserEmail_thenThrowRuntimeException() {
        when(mockUserRepository.save(testUser1)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class,
                () -> userService.save(UserDtoMapper.toUserDto(testUser1)));
    }

    @Test
    void update_whenValidId_thenUpdateFields() {
        UserDto userDto = UserDtoMapper.toUserDto(testUser1);
        User user = new User(1L, "updated", "updated@ya.ru");

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mockUserRepository.save(user)).thenReturn(user);

        UserDto result = userService.update(1L, userDto);

        verify(mockUserRepository, times(1)).findById(1L);
        verify(mockUserRepository, times(1)).save(user);

        assertEquals(1L, result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void update_whenInvalidId_thenThrowsObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> userService.find(1L));

        verify(mockUserRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getAll_thenReturnsAllUsers() {
        List<User> userList = Arrays.asList(testUser1, testUser2);
        when(mockUserRepository.findAll()).thenReturn(userList);

        List<UserDto> result = userService.getAll();
        assertEquals(2, result.size());

        UserDto userDto1 = result.get(0);
        assertEquals(testUser1.getId(), userDto1.getId());
        assertEquals(testUser1.getName(), userDto1.getName());

        UserDto userDto2 = result.get(1);
        assertEquals(testUser2.getId(), userDto2.getId());
        assertEquals(testUser2.getName(), userDto2.getName());
    }

    @Test
    void delete_whenUserNotFound_thenObjectNotFoundException() {
        when(mockUserRepository.findById(999L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> userService.delete(999));

        assertEquals("Пользователь id: " + 999 + " не найден или ещё не создан.", objectNotFoundException.getMessage());

    }

}