package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
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
    public ItemDto update(long ownerId, ItemDto itemDto, long itemId) {
        Item item = ItemDtoMapper.mapToItem(itemDto);
        item.setId(itemId);
        item.setOwner(userStorage.getById(ownerId));

        return ItemDtoMapper.mapToItemDto(itemStorage.update(item, itemId));
    }

    @Override
    public ItemDto getById(long itemId) {
        return ItemDtoMapper.mapToItemDto(itemStorage.getById(itemId));
    }

    @Override
    public Collection<ItemDto> getAll(long ownerId) {
        return itemStorage.getAll(ownerId).stream()
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return itemStorage.search(text).stream()
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
