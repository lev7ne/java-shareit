package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoRequest {
    private long id;
    @NotBlank(message = "Название вещи не может быть пустым.")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым.")
    private String description;
    @NotNull
    private Boolean available;
}