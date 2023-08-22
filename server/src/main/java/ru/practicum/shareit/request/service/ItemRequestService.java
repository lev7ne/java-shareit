package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoResponse add(long id, ItemRequestDtoRequest itemRequestDtoRequest);

    List<ItemRequestDtoResponse> findByRequesterId(long requesterId);

    ItemRequestDtoResponse findRequestByUserIdAndRequestId(long userId, long requestId);

    List<ItemRequestDtoResponse> findAllByRequesterId(long ownerId, Integer from, Integer size);


}
