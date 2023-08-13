package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemDtoMapper {
    public Item mapToItem(ItemDtoRequest itemDtoRequest) {
        return new Item(
                itemDtoRequest.getId(),
                itemDtoRequest.getName(),
                itemDtoRequest.getDescription(),
                itemDtoRequest.getAvailable()
        );
    }

    public ItemDtoResponse mapToItemDtoResponse(Item item) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null,
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

    public ItemDtoResponse mapToItemDtoResponseExtended(Item item, BookingDtoResponseShort lastBooking, BookingDtoResponseShort nextBooking, List<CommentDto> comments) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments,
                item.getItemRequest() != null ? item.getItemRequest().getId() : null
        );
    }

//    public ItemDtoResponse toItemDtoResponseRequest(Item item) {
//        return new ItemDtoResponse(
//                item.getId(),
//                item.getName(),
//                item.getDescription(),
//                item.getAvailable(),
//                item.getOwner().getId()
//        );
//    }

}
