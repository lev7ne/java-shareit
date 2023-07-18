package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Counter;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    Map<Long, Item> itemMap = new HashMap<>();
    Counter counter = new Counter();

    @Override
    public Item add(Item item) {
        item.setId(counter.createId());
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId) {
        Item updatedItem = itemMap.get(itemId);
        if (updatedItem == null) {
            log.error("Предмет с id: {} не существует", itemId);
            throw new NotFoundException("Попытка получить предмет с несуществующим id: " + itemId);
        }

        if (item.getOwner().getId() != updatedItem.getOwner().getId()) {
            log.error("Только владельцу вещи разрешено редактирование.");
            throw new NoAccessException("Только владельцу вещи разрешено редактирование.");
        }

        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public Collection<Item> getAll(long ownerId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(long id) {
        if (!itemMap.containsKey(id)) {
            throw new NotFoundException("Попытка получить предмет с несуществующим id: " + id);
        }
        return itemMap.get(id);
    }
}
