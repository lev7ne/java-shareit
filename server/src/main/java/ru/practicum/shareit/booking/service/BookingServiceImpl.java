package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.AccessDeniedException;
import ru.practicum.shareit.util.exception.ObjectNotFoundException;
import ru.practicum.shareit.util.exception.UnavailableException;
import ru.practicum.shareit.util.exception.UnavailableStateException;
import ru.practicum.shareit.util.validator.ObjectHelper;
import ru.practicum.shareit.util.validator.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.*;


@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDtoResponse add(long bookerId, BookingDtoRequest bookingDtoRequest) {
        User booker = ObjectHelper.findUserById(userRepository, bookerId);
        Item item = ObjectHelper.findItemById(itemRepository, bookingDtoRequest.getItemId());

        if (booker.getId() == item.getOwner().getId()) {
            throw new ObjectNotFoundException("Нельзя забронировать свой предмет.");
        }
        if (!item.getAvailable()) {
            throw new UnavailableException(item.getName() + " - забронирован другим пользователем.");
        }

        Validator.startAndEndTimeBookingValidation(bookingDtoRequest);
        Booking booking = BookingDtoMapper.toBooking(bookingDtoRequest, item, booker);

        return BookingDtoMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse update(long ownerId, long bookingId, boolean approved) {
        ObjectHelper.findUserById(userRepository, ownerId);
        Booking booking = ObjectHelper.findBookingById(bookingRepository, bookingId);

        if (booking.getBookingStatus() != WAITING) {
            throw new UnavailableException("Запрос на бронирование уже был обработан ранее.");
        }
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new AccessDeniedException("Только владелец вещи может работать с запросом.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new UnavailableException(booking.getItem().getName() + " забронирован ранее.");
        }

        booking.setBookingStatus(approved ? APPROVED : REJECTED);

        return BookingDtoMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse find(long userId, long bookingId) {
        ObjectHelper.findUserById(userRepository, userId);
        Booking booking = ObjectHelper.findBookingById(bookingRepository, bookingId);

        if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwner().getId()) {
            return BookingDtoMapper.toBookingDtoResponse(booking);
        } else {
            throw new AccessDeniedException("Просмотр разрешен только владельцу предмета или бронирования.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> readAllBookingsBooker(long bookerId, BookingState state, Integer from, Integer size) {
        ObjectHelper.findUserById(userRepository, bookerId);

        Pageable page = ObjectHelper.getPageRequest(from, size).withSort(Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllBookingsBookerCurrent(bookerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllBookingsBookerPast(bookerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllBookingsBookerFuture(bookerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllBookingsByBookingStatusForBooker(bookerId, state.toString(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case ALL:
                return bookingRepository.findAllByBookerId(bookerId, page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            default:
                throw new UnavailableStateException("Недопустимый параметр state.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDtoResponse> readAllBookingsOwner(long ownerId, BookingState state, Integer from, Integer size) {
        ObjectHelper.findUserById(userRepository, ownerId);

        Pageable page = ObjectHelper.getPageRequest(from, size).withSort(Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllBookingsOwnerCurrent(ownerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllBookingsOwnerPast(ownerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllBookingsOwnerFuture(ownerId, LocalDateTime.now(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllBookingsByBookingStatusForOwner(ownerId, state.toString(), page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case ALL:
                return bookingRepository.findAllByOwnerId(ownerId, page)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            default:
                throw new UnavailableStateException("Недопустимый параметр state.");
        }
    }

}
