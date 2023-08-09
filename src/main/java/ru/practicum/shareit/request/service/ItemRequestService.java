package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse add(long id, ItemRequestDtoRequest itemRequestDtoRequest);

    List<ItemRequestDtoResponse> findAllByUserId(long ownerId);

    ItemRequestDtoResponse find(long userId, long requestId);
}
