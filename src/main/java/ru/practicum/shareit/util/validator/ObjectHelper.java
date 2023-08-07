package ru.practicum.shareit.util.validator;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ObjectNotFoundException;

@UtilityClass
public class ObjectHelper {
    public User findUserById(UserRepository repository, long id) {
        return repository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Пользователь id: " + id + " не найден или ещё не создан."));
    }

    public Item findItemById(ItemRepository itemRepository, long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Предмет id: " + id + " не найден или ещё не создан."));
    }

    public Booking findBookingById(BookingRepository bookingRepository, long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Бронирование id: " + id + " не найдено или ещё не создано."));
    }
}