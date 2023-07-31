package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.model.User;

public class BookingDtoMapper {
    public static Booking toBookingDto(BookingDto bookingDto, Item item, User user, BookingStatus status) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                status
        );
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBookingStatus(),
                new UserDto(booking.getBooker().getId(), null, null),
                new ItemDto(booking.getItem().getId(), booking.getItem().getName(), null, null)
        );
    }
}
