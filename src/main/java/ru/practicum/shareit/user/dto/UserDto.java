package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    long id;
    @NotBlank(message = "Имя не может быть пустым!")
    String name;
    @NotBlank(message = "Email не может быть пустым!")
    @Email(message = "Некорректный email!")
    String email;
}
