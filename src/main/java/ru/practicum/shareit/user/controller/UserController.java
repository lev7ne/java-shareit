package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.add(userDto);
    }
    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer id) {
        return userService.update(userDto, id);
    }
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        return userService.getById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.delete(id);
    }
    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getAll();
    }
}
