package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;

public class ItemDtoMapper {
    public static Item mapToItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static ItemDtoBooking mapToItemDtoBooking(Item item, Booking booking) {
        return new ItemDtoBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                booking != null ? Instant.from(booking.getStart()) : null,
                booking != null ? Instant.from(booking.getEnd()) : null
        );
    }

    public static ItemDto mapToItemDtoWithoutDescAndAvailable(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                null,
                null
        );
    }
}
