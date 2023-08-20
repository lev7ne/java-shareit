package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Email не может быть пустым!")
    @Email(message = "Некорректный email!")
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
