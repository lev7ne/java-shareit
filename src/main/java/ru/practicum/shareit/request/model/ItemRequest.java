package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    long id;
    String description;
    User requestor;
    LocalDate created;
}
