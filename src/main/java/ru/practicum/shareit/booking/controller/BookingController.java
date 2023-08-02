package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                            @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        return bookingService.add(bookerId, bookingDtoRequest);
    }

    @PatchMapping("/{id}")
    public BookingDtoResponse updateApproval(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @PathVariable long id,
                                             @RequestParam("approved") boolean approved) {
        return bookingService.update(ownerId, id, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse readAnyBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        return bookingService.findAny(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> readAllBookerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        BookingState validState = BookingState.getBookingState(state);
        return bookingService.readAllBookingsBooker(userId, validState);
    }

    @GetMapping("/bookings/owner")
    public Collection<BookingDtoResponse> readAllBookingItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(defaultValue = "ALL") String state) {
        BookingState validState = BookingState.getBookingState(state);
        return null;
    }
}
