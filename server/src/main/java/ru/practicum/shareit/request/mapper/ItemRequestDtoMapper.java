package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class ItemRequestDtoMapper {
    public ItemRequest toItemRequest(ItemRequestDtoRequest itemRequestDtoRequest, User user, LocalDateTime now) {
        return new ItemRequest(
                itemRequestDtoRequest.getDescription(),
                user,
                now
        );
    }

    public ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                null
        );
    }

}
