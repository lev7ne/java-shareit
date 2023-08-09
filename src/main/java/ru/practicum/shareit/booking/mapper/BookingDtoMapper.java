package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoResponseShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingDtoMapper {
    public Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User user) {
        return new Booking(
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public BookingDtoResponseShort toBookingDtoResponseShort(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDtoResponseShort(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBookingStatus(),
                new UserDto(booking.getBooker().getId(), null, null),
                new ItemDtoRequest(booking.getItem().getId(), booking.getItem().getName(), null, null, 0)
        );
    }
}
