package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    private String email;
}
