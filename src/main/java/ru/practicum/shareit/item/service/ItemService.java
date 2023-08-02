package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.Collection;


public interface ItemService {
    ItemDto add(long ownerId, ItemDto itemDto);

    ItemDto update(long itemId, ItemDto itemDto, long ownerId);

    ItemDtoBooking getById(long itemId, long userId);

    Collection<ItemDto> getAll(long ownerId);

    Collection<ItemDto> search(String text);

    ItemDtoBooking getItemByIdWithUser(long userId, long id);
}
