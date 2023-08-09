package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDtoRequest {
    @NotNull(message = "Описание запроса не может быть пустым.")
    String description;
}
