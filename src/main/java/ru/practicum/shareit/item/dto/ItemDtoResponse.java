package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;

import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemDtoResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoResponseShort lastBooking;
    private BookingDtoResponseShort nextBooking;
    private Collection<CommentDto> comments;
}
