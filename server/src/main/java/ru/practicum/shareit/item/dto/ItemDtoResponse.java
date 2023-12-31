package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoResponse {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoResponseShort lastBooking;
    private BookingDtoResponseShort nextBooking;
    private List<CommentDto> comments;
    private Long requestId;

    public ItemDtoResponse(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
