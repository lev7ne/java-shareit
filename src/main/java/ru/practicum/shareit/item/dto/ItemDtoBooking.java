package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDtoBooking extends ItemDto {
    private final Instant start;
    private final Instant end;

    public ItemDtoBooking(
            long id,
            String name,
            String description,
            Boolean available,
            Instant start,
            Instant end
    ) {
        super(id, name, description, available);
        this.start = start;
        this.end = end;
    }
}

