package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.AccessDeniedException;
import ru.practicum.shareit.util.exception.UnavailableException;
import ru.practicum.shareit.util.validator.ObjectHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDtoResponse save(long ownerId, ItemDtoRequest itemDto) {
        User owner = ObjectHelper.findUserById(userRepository, ownerId);

        Item item = ItemDtoMapper.mapToItem(itemDto);
        item.setOwner(owner);

        return ItemDtoMapper.mapToItemDtoResponse(itemRepository.save(item));
    }

    @Override
    public ItemDtoResponse update(long ownerId, ItemDtoRequest itemDto, long itemId) {
        Item updatedItem = ObjectHelper.findItemById(itemRepository, itemId);

        if (updatedItem.getOwner().getId() != ownerId) {
            throw new AccessDeniedException("Только владельцу предмета разрешено редактирование.");
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

        return ItemDtoMapper.mapToItemDtoResponse(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDtoResponse find(long itemId, long userId) {
        Item item = ObjectHelper.findItemById(itemRepository, itemId);

        BookingDtoResponseShort lastBooking = null;
        BookingDtoResponseShort nextBooking = null;

        if (item.getOwner().getId() == userId) {
            lastBooking = findLastBooking(itemId) != null ? BookingDtoMapper.toBookingDtoResponseShort(findLastBooking(itemId)) : null;
            nextBooking = findNextBooking(itemId) != null ? BookingDtoMapper.toBookingDtoResponseShort(findNextBooking(itemId)) : null;
        }

        Collection<CommentDto> comments = findComments(itemId);

        return ItemDtoMapper.mapToItemDtoResponseExtended(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemDtoResponse> getAll(long ownerId) {
        return itemRepository.findAllByOwner_IdOrderById(ownerId).stream()
                .map(item -> {
                    Booking lastBooking = findLastBooking(item.getId());
                    Booking nextBooking = findNextBooking(item.getId());
                    Collection<CommentDto> comments = findComments(item.getId());
                    return ItemDtoMapper.mapToItemDtoResponseExtended(item,
                            lastBooking != null ? BookingDtoMapper.toBookingDtoResponseShort(lastBooking) : null,
                            nextBooking != null ? BookingDtoMapper.toBookingDtoResponseShort(nextBooking) : null,
                            comments.isEmpty() ? null : comments);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDtoResponse> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemDtoMapper::mapToItemDtoResponse)
                .collect(Collectors.toList());
    }

    private Booking findLastBooking(long id) {
        return bookingRepository.findAllByItem_Id(id).stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getBookingStatus() == BookingStatus.APPROVED)
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
    }

    private Booking findNextBooking(long id) {
        return bookingRepository.findAllByItem_Id(id).stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getBookingStatus() == BookingStatus.APPROVED)
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
    }

    @Override
    public CommentDto saveComment(long bookerId, CommentDto commentDto, long itemId) {
        User booker = ObjectHelper.findUserById(userRepository, bookerId);

        Collection<Booking> itemBookings = bookingRepository.findAllByItem_Id(itemId)
                .stream()
                .filter(booking -> booking.getBooker().getId() == bookerId)
                .filter(booking -> booking.getBookingStatus() == BookingStatus.APPROVED)
                .collect(Collectors.toList());

        if (itemBookings.isEmpty()) {
            throw new UnavailableException("Пользователь с id: " + bookerId + " не бронировал вещь с id: " + itemId);
        }

        Collection<Booking> pastOrPresentBookings = itemBookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (pastOrPresentBookings.isEmpty()) {
            throw new UnavailableException("Отзыв можно оставить только после состоявшегося бронирования.");
        }

        Item item = ObjectHelper.findItemById(itemRepository, itemId);

        commentDto.setCreated(LocalDateTime.now());

        Comment comment = CommentDtoMapper.mapToComment(commentDto, item, booker);

        return CommentDtoMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private Collection<CommentDto> findComments(long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentDtoMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }
}
