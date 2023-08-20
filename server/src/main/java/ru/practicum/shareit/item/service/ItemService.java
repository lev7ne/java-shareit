package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;


public interface ItemService {
    ItemDtoResponse save(long ownerId, ItemDtoRequest itemDto);

    ItemDtoResponse update(long itemId, ItemDtoRequest itemDto, long ownerId);

    ItemDtoResponse find(long itemId, long userId);

    List<ItemDtoResponse> getAll(long bookerId, Integer from, Integer size);

    List<ItemDtoResponse> search(String text, int from, int size);

    CommentDto saveComment(long authorId, CommentDto commentDto, long itemId);

}
