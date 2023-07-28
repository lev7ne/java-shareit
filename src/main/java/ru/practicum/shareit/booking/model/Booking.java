package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    long id;
    Instant start;
    Instant end;
    Item item;
    User booker;
    Status status;
}
