package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoRequest {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;

    public ItemDtoRequest(String name, String description, Boolean available, long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    public ItemDtoRequest(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
