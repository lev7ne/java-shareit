package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.Collection;


public interface ItemService {
    ItemDtoResponse save(long ownerId, ItemDtoRequest itemDto);

    ItemDtoResponse update(long itemId, ItemDtoRequest itemDto, long ownerId);

    ItemDtoResponse find(long itemId, long userId);

    Collection<ItemDtoResponse> getAll(long ownerId);

    Collection<ItemDtoResponse> search(String text);

    CommentDto saveComment(long authorId, CommentDto commentDto, long itemId);

}
