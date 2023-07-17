package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.exception.NoAccessException;

@Service
public class ItemServiceImpl implements ItemService{
    ItemStorage itemStorage;
    UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto add(long ownerId, ItemDto itemDto) {
        Item item = ItemDtoMapper.mapToItem(itemDto);
        item.setOwner(userStorage.getById(ownerId));
        return ItemDtoMapper.mapToItemDto(itemStorage.add(item));
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long ownerId) {
        Item item = ItemDtoMapper.mapToItem(getById(itemId));
        if (item.getId() != ownerId) {
            throw new NoAccessException("Только владельцу вещи разрешено редактирование.");
        }
        return null;
    }

    @Override
    public ItemDto getById(long itemId) {
        return ItemDtoMapper.mapToItemDto(itemStorage.getById(itemId));
    }
}
