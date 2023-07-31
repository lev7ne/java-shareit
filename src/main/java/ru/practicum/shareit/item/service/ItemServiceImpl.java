package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemDtoBookingExtended;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto add(long ownerId, ItemDto itemDto) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + ownerId + " не найден или ещё не создан.");
        }
        Item item = ItemDtoMapper.mapToItem(itemDto);
        item.setOwner(optionalUser.get());

        return ItemDtoMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long ownerId, ItemDto itemDto, long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с id: " + itemId + " не найден или ещё не создан.");
        }

        Item updatedItem = optionalItem.get();

        if (optionalItem.get().getOwner().getId() != ownerId) {
            throw new NoAccessException("Только владельцу вещи разрешено редактирование.");
        }

        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }

        return ItemDtoMapper.mapToItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDtoBooking getById(long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с id: " + itemId + " не найден или ещё не создан.");
        }
        Booking booking = bookingRepository.findByItemId(itemId);
        return ItemDtoMapper.mapToItemDtoBooking(optionalItem.get(), booking);
    }

    @Override
    public Collection<ItemDto> getAll(long ownerId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemDtoMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoBookingExtended getItemByIdWithUser(long ownerId, long itemId) {
        Item item = itemRepository.findItemByOwner_IdAndId(ownerId, itemId);



        return null;
    }
}
