package ru.practicum.shareit.booking.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                            @RequestBody BookingDtoRequest bookingDtoRequest) {
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
        return bookingService.find(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> readAllBookerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        return bookingService.readAllBookingsBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> readAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                        @RequestParam(defaultValue = "ALL") BookingState state,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        return bookingService.readAllBookingsOwner(ownerId, state, from, size);
    }
}
