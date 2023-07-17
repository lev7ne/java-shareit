package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Counter;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

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
    public Item update() {
        


        return null;
    }

    @Override
    public Item getById(long id) {
        if (!itemMap.containsKey(id)) {
            throw new NotFoundException("Попытка получить предмет с несуществующим id: " + id);
        }
        return itemMap.get(id);
    }
}
