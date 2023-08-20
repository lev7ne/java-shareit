package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                           @RequestBody @Valid BookingDtoRequest bookingDtoRequest) {
        return bookingClient.bookItem(bookerId, bookingDtoRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateApproval(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                 @PathVariable long id,
                                                 @RequestParam("approved") boolean approved) {
        return bookingClient.updateApproval(ownerId, id, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> readAnyBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> readAllBookerBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingClient.getBookingsForBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> readAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return bookingClient.getBookingsForOwner(ownerId, state, from, size);
    }
}
