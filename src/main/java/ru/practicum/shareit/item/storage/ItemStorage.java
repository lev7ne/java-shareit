package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item add(Item item);

    Item update(Item item, long itemId);

    Item getById(long id);

    Collection<Item> getAll(long ownerId);

    Collection<Item> search(String text);
}
