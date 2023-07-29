package ru.practicum.shareit.booking.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
public class BookingDtoResponse {
    long id;
    LocalDate start;
    LocalDate end;
    Status status;
    UserDto booker;
    ItemDto item;
}
