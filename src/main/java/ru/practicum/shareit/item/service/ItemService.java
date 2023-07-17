package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;


public interface ItemService {
    ItemDto add(long ownerId, ItemDto itemDto);
    ItemDto update(long itemId, ItemDto itemDto, long ownerId);
    ItemDto getById(long itemId);
}
