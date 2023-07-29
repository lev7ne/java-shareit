package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.NotFoundException;

import java.util.Optional;

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

        Booking booking = BookingDtoMapper.mapToBooking(bookingDto, optionalItem.get(), optionalUser.get(), Status.WAITING);

        BookingDtoResponse bookingDtoResponse = BookingDtoMapper.mapToBookingResponse(bookingRepository.save(booking), optionalUser.get(), optionalItem.get());

        return bookingDtoResponse;
    }
}
