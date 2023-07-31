package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.BookingUnavailableException;
import ru.practicum.shareit.util.exception.NoAccessException;
import ru.practicum.shareit.util.exception.NotFoundException;
import ru.practicum.shareit.util.exception.UnavailableStateException;
import ru.practicum.shareit.util.validator.PeriodValidator;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public BookingDtoResponse add(long bookerId, BookingDto bookingDto) {
        Optional<User> optionalUser = userRepository.findById(bookerId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + bookerId + " не найден или ещё не создан.");
        }

        Optional<Item> optionalItem = itemRepository.findById(bookingDto.getItemId());
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с id: " + bookingDto.getItemId() + " не найден или ещё не создан.");
        }

        if (optionalUser.get().getId() == optionalItem.get().getOwner().getId()) {
            throw new BookingUnavailableException("Нельзя забронировать свой предмет.");
        }

        if (!optionalItem.get().getAvailable()) {
            throw new BookingUnavailableException(optionalItem.get().getName() + " - уже находится в брони.");
        }

        PeriodValidator.StartAndEndTimeValidation(bookingDto);
        Booking booking = BookingDtoMapper.toBookingDto(bookingDto, optionalItem.get(), optionalUser.get(), BookingStatus.WAITING);

        return BookingDtoMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse update(long ownerId, long id, boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Бронирование с id: " + id + " не найдено или ещё не создано.");
        }

        Booking booking = optionalBooking.get();

        if (booking.getBookingStatus().equals(BookingStatus.APPROVED) && approved) {
            throw new BookingUnavailableException("Бронирование подтверждено ранее.");
        }

        if (optionalBooking.get().getItem().getOwner().getId() != ownerId) {
            throw new NoAccessException("Только владелец вещи может работать с запросом.");
        }

        if (!booking.getItem().getAvailable()) {
            throw new BookingUnavailableException(booking.getItem().getName() + " забронирован ранее.");
        }

        Optional<User> optionalUser = userRepository.findById(booking.getBooker().getId());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + booking.getBooker().getId() + " не найден или ещё не создан.");
        }

        booking.setBookingStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingDtoMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse findAny(long ownerId, long bookingId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + ownerId + " не найден или ещё не создан.");
        }

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Бронирование с id: " + bookingId + " не найдено или ещё не создано.");
        }

        Booking booking = optionalBooking.get();

        long anyBookerId = booking.getBooker().getId();
        long anyOwnerId = booking.getItem().getOwner().getId();

        if (anyBookerId != ownerId && anyOwnerId != ownerId) {
            throw new NotFoundException("Бронирование с id " + bookingId + " не найдено для пользователя с id " + ownerId);
        }

        Optional<User> optionalUser = userRepository.findById(anyBookerId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + anyBookerId + " не найден или ещё не создан.");
        }
        Optional<Item> optionalItem = itemRepository.findById(booking.getItem().getId());
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Предмет с id: " + booking.getItem().getId() + " не найден или ещё не создан.");
        }

        return BookingDtoMapper.toBookingDtoResponse(booking);
    }

    @Override
    public Collection<BookingDtoResponse> readAllBookingsBooker(long bookerId, BookingState state) {
        Optional<User> owner = userRepository.findById(bookerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + bookerId + " не найден или ещё не создан.");
        }

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllBookingsBookerCurrent(bookerId, LocalDateTime.now())
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllBookingsBookerPast(bookerId, LocalDateTime.now())
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllBookingsBookerFuture(bookerId, LocalDateTime.now())
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllBookingsByBookingStatus(bookerId, state.toString())
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(bookerId)
                        .stream()
                        .map(BookingDtoMapper::toBookingDtoResponse)
                        .collect(Collectors.toList());
            default:
                throw new UnavailableStateException("Недопустимый параметр state.");
        }
    }

    public Collection<BookingDtoResponse> readAllBookingsOwner(long ownerId, BookingState state) {
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь с id: " + ownerId + " не найден или ещё не создан.");
        }
        
        
        return null;
    }
    


}
