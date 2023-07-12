package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class Item {
    long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;
}
