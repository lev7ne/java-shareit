package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@Slf4j
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
        List<CommentDto> comments = findComments(itemId);

        if (item.getOwner().getId() == userId) {
            return ItemDtoMapper.mapToItemDtoResponseExtended(
                    item,
                    BookingDtoMapper.toBookingDtoResponseShort(findPrevious(itemId)),
                    BookingDtoMapper.toBookingDtoResponseShort(findNext(itemId)),
                    comments);
        }
        return ItemDtoMapper.mapToItemDtoResponseExtended(item, null, null, comments);
    }

    private Booking findPrevious(long itemId) {
        Sort desc = Sort.by(Sort.Direction.DESC, "end");
        List<Booking> bookingsBeforeNow = bookingRepository.findLastApprovedBookingByItemId(itemId, LocalDateTime.now(), APPROVED, desc);
        if (bookingsBeforeNow.isEmpty()) {
            return null;
        }
        return bookingsBeforeNow.get(0);
    }

    private Booking findNext(long itemId) {
        Sort asc = Sort.by(Sort.Direction.ASC, "start");
        List<Booking> bookingsAfterNow = bookingRepository.findNextApprovedBookingByItemId(itemId, LocalDateTime.now(), APPROVED, asc);
        if (bookingsAfterNow.isEmpty()) {
            return null;
        }
        return bookingsAfterNow.get(0);
    }


    @Override
    public List<ItemDtoResponse> getAll(long ownerId) {
        List<Item> itemsList = itemRepository.getItemsByOwner_Id(ownerId);

        List<Long> itemIdsList = itemsList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> commentsList = commentRepository.findAllByItemIds(itemIdsList);

        Map<Long, List<Comment>> itemIdWithCommentsMap = commentsList.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<Booking> bookingsListApproved = bookingRepository.findAllByItemIdAndBookingStatus(itemIdsList, APPROVED.toString());

        Map<Long, List<Booking>> itemIdWithBookingApprovedMap = bookingsListApproved.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return itemsList.stream()
                .map(item -> {

                    List<Booking> bookings = itemIdWithBookingApprovedMap.get(item.getId());

                    Booking lastBooking = null;
                    Booking nextBooking = null;
                    if (bookings != null) {
                        lastBooking = bookings.stream()
                                .filter(booking -> booking.getItem().getId() == item.getId())
                                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                                .max(Comparator.comparing(Booking::getEnd))
                                .orElse(null);

                        nextBooking = bookings.stream()
                                .filter(booking -> booking.getItem().getId() == item.getId())
                                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                                .min(Comparator.comparing(Booking::getStart))
                                .orElse(null);
                    }

                    List<Comment> comments = itemIdWithCommentsMap.get(item.getId());

                    List<CommentDto> commentDtos = null;
                    if (comments != null) {
                        commentDtos = comments.stream()
                                .filter(comment -> comment.getItem().getId() == item.getId())
                                .map(CommentDtoMapper::mapToCommentDto)
                                .collect(Collectors.toList());
                    }
                    return ItemDtoMapper.mapToItemDtoResponseExtended(
                            item,
                            lastBooking != null ? BookingDtoMapper.toBookingDtoResponseShort(lastBooking) : null,
                            nextBooking != null ? BookingDtoMapper.toBookingDtoResponseShort(nextBooking) : null,
                            commentDtos);
                })
                .sorted(Comparator.comparing(ItemDtoResponse::getId))
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

    @Override
    public CommentDto saveComment(long bookerId, CommentDto commentDto, long itemId) {
        User booker = ObjectHelper.findUserById(userRepository, bookerId);

        Collection<Booking> itemBookings = bookingRepository.findAllByItem_Id(itemId)
                .stream()
                .filter(booking -> booking.getBooker().getId() == bookerId)
                .filter(booking -> booking.getBookingStatus() == APPROVED)
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

    private List<CommentDto> findComments(long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentDtoMapper::mapToCommentDto)
                .collect(Collectors.toList());
    }
}
