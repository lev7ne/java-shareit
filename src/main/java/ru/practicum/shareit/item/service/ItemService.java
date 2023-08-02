package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.Collection;


public interface ItemService {
    ItemDtoResponse add(long ownerId, ItemDtoRequest itemDto);

    ItemDtoResponse update(long itemId, ItemDtoRequest itemDto, long ownerId);

    ItemDtoResponse getAny(long itemId, long userId);

    Collection<ItemDtoResponse> getAll(long ownerId);

    Collection<ItemDtoResponse> search(String text);

    ItemDtoResponse getItemByIdWithUser(long userId, long id);
}
