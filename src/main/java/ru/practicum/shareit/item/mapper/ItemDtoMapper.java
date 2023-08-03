package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public class ItemDtoMapper {
    public static Item mapToItem(ItemDtoRequest itemDtoRequest) {
        return new Item(
                itemDtoRequest.getId(),
                itemDtoRequest.getName(),
                itemDtoRequest.getDescription(),
                itemDtoRequest.getAvailable()
        );
    }

    public static ItemDtoResponse mapToItemDtoResponse(Item item) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static ItemDtoResponse mapToItemDtoResponseExtended(Item item, BookingDtoResponseShort lastBooking, BookingDtoResponseShort nextBooking, Collection<CommentDto> comments) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments
        );
    }

}
