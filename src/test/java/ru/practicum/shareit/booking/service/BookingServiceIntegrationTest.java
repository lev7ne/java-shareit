package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.booking.model.BookingState.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    UserDto userDto1;
    UserDto userDto2;
    ItemDtoRequest itemDtoRequest1;
    ItemDtoRequest itemDtoRequest2;
    BookingDtoRequest bookingDtoRequestPast;
    BookingDtoRequest bookingDtoRequestCurrent;
    BookingDtoRequest bookingDtoRequestFuture;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto("user1", "email1@ya.ru");
        userDto2 = new UserDto("user2", "email2@ya.ru");

        itemDtoRequest1 = new ItemDtoRequest("item1", "description1", true);
        itemDtoRequest2 = new ItemDtoRequest("item2", "description2", true);

        LocalDateTime startPast = LocalDateTime.of(2023, 8, 1, 10, 0);
        LocalDateTime endPast = LocalDateTime.of(2023, 8, 2, 10, 0);
        bookingDtoRequestPast = new BookingDtoRequest(startPast, endPast, 1);

        LocalDateTime startCurrent = LocalDateTime.now();
        LocalDateTime endCurrent = startCurrent.plusHours(1);
        bookingDtoRequestCurrent = new BookingDtoRequest(startCurrent, endCurrent, 1);

        LocalDateTime startFuture = LocalDateTime.of(2024, 8, 1, 10, 0);
        LocalDateTime endFuture = LocalDateTime.of(2024, 8, 2, 10, 0);
        bookingDtoRequestFuture = new BookingDtoRequest(startFuture, endFuture, 1);
    }

    @Test
    void readAllBookingsBooker() {
        UserDto savedOwner = userService.save(userDto1);
        UserDto savedBooker = userService.save(userDto2);
        itemService.save(savedOwner.getId(), itemDtoRequest1);
        itemService.save(savedOwner.getId(), itemDtoRequest2);
        BookingDtoResponse savedPastBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestPast);
        BookingDtoResponse savedCurrentBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestCurrent);
        BookingDtoResponse savedFutureBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestFuture);

        List<BookingDtoResponse> expectedListPastBooking = List.of(savedPastBooking);
        List<BookingDtoResponse> expectedListCurrentBooking = List.of(savedCurrentBooking);
        List<BookingDtoResponse> expectedListFutureBooking = List.of(savedFutureBooking);
        List<BookingDtoResponse> expectedListAllBooking = List.of(savedPastBooking, savedCurrentBooking, savedFutureBooking);

        List<BookingDtoResponse> returnedListPastBooking = bookingService.readAllBookingsBooker(savedBooker.getId(), PAST, 0, 10);
        List<BookingDtoResponse> returnedListCurrentBooking = bookingService.readAllBookingsBooker(savedBooker.getId(), CURRENT, 0, 10);
        List<BookingDtoResponse> returnedListFutureBooking = bookingService.readAllBookingsBooker(savedBooker.getId(), FUTURE, 0, 10);
        List<BookingDtoResponse> returnedListAllBooking = bookingService.readAllBookingsBooker(savedBooker.getId(), ALL, 0, 10);

        assertEquals(expectedListPastBooking, returnedListPastBooking);

        assertEquals(expectedListCurrentBooking.get(0).getId(), returnedListCurrentBooking.get(0).getId());
        assertEquals(expectedListCurrentBooking.get(0).getBooker(), returnedListCurrentBooking.get(0).getBooker());
        assertEquals(expectedListCurrentBooking.get(0).getItem(), returnedListCurrentBooking.get(0).getItem());
        assertTrue(Duration.between(expectedListCurrentBooking.get(0).getStart(), returnedListCurrentBooking.get(0).getStart()).toMillis() < 1000);
        assertTrue(Duration.between(expectedListCurrentBooking.get(0).getEnd(), returnedListCurrentBooking.get(0).getEnd()).toMillis() < 1000);

        assertEquals(expectedListFutureBooking.get(0).getId(), returnedListFutureBooking.get(0).getId());
        assertEquals(expectedListFutureBooking.get(0).getBooker(), returnedListFutureBooking.get(0).getBooker());
        assertEquals(expectedListFutureBooking.get(0).getItem(), returnedListFutureBooking.get(0).getItem());
        assertEquals(expectedListFutureBooking.get(0).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                returnedListFutureBooking.get(0).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertEquals(expectedListFutureBooking.get(0).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                returnedListFutureBooking.get(0).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        assertEquals(expectedListAllBooking.size(), returnedListAllBooking.size());

        BookingDtoResponse updatedBooking2 = bookingService.update(savedOwner.getId(), savedFutureBooking.getId(), false);

        List<BookingDtoResponse> expectedListApprovedBooking = List.of(savedPastBooking, savedCurrentBooking);
        List<BookingDtoResponse> expectedListRejectedBooking = List.of(updatedBooking2);

        List<BookingDtoResponse> returnedListApprovedBooking = bookingService.readAllBookingsBooker(savedBooker.getId(), WAITING, 0, 10);
        List<BookingDtoResponse> returnedListRejectedBooking = bookingService.readAllBookingsBooker(savedBooker.getId(), REJECTED, 0, 10);

        assertEquals(expectedListApprovedBooking.size(), returnedListApprovedBooking.size());
        assertEquals(expectedListRejectedBooking.size(), returnedListRejectedBooking.size());
        assertEquals(expectedListRejectedBooking.get(0).getStatus(), returnedListRejectedBooking.get(0).getStatus());
    }

    @Test
    void readAllBookingsOwner() {
        UserDto savedOwner = userService.save(userDto1);
        UserDto savedBooker = userService.save(userDto2);
        itemService.save(savedOwner.getId(), itemDtoRequest1);
        itemService.save(savedOwner.getId(), itemDtoRequest2);
        BookingDtoResponse savedPastBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestPast);
        BookingDtoResponse savedCurrentBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestCurrent);
        BookingDtoResponse savedFutureBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestFuture);

        List<BookingDtoResponse> expectedListPastBooking = List.of(savedPastBooking);
        List<BookingDtoResponse> expectedListCurrentBooking = List.of(savedCurrentBooking);
        List<BookingDtoResponse> expectedListFutureBooking = List.of(savedFutureBooking);
        List<BookingDtoResponse> expectedListAllBooking = List.of(savedPastBooking, savedCurrentBooking, savedFutureBooking);

        List<BookingDtoResponse> returnedListPastBooking = bookingService.readAllBookingsOwner(savedOwner.getId(), PAST, 0, 10);
        List<BookingDtoResponse> returnedListCurrentBooking = bookingService.readAllBookingsOwner(savedOwner.getId(), CURRENT, 0, 10);
        List<BookingDtoResponse> returnedListFutureBooking = bookingService.readAllBookingsOwner(savedOwner.getId(), FUTURE, 0, 10);
        List<BookingDtoResponse> returnedListAllBooking = bookingService.readAllBookingsOwner(savedOwner.getId(), ALL, 0, 10);

        assertEquals(expectedListPastBooking, returnedListPastBooking);

        assertEquals(expectedListCurrentBooking.get(0).getId(), returnedListCurrentBooking.get(0).getId());
        assertEquals(expectedListCurrentBooking.get(0).getBooker(), returnedListCurrentBooking.get(0).getBooker());
        assertEquals(expectedListCurrentBooking.get(0).getItem(), returnedListCurrentBooking.get(0).getItem());
        assertTrue(Duration.between(expectedListCurrentBooking.get(0).getStart(), returnedListCurrentBooking.get(0).getStart()).toMillis() < 1000);
        assertTrue(Duration.between(expectedListCurrentBooking.get(0).getEnd(), returnedListCurrentBooking.get(0).getEnd()).toMillis() < 1000);

        assertEquals(expectedListFutureBooking.get(0).getId(), returnedListFutureBooking.get(0).getId());
        assertEquals(expectedListFutureBooking.get(0).getBooker(), returnedListFutureBooking.get(0).getBooker());
        assertEquals(expectedListFutureBooking.get(0).getItem(), returnedListFutureBooking.get(0).getItem());
        assertEquals(expectedListFutureBooking.get(0).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                returnedListFutureBooking.get(0).getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        assertEquals(expectedListFutureBooking.get(0).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                returnedListFutureBooking.get(0).getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        assertEquals(expectedListAllBooking.size(), returnedListAllBooking.size());

        BookingDtoResponse updatedBooking2 = bookingService.update(savedOwner.getId(), savedFutureBooking.getId(), false);

        List<BookingDtoResponse> expectedListApprovedBooking = List.of(savedPastBooking, savedCurrentBooking);
        List<BookingDtoResponse> expectedListRejectedBooking = List.of(updatedBooking2);

        List<BookingDtoResponse> returnedListApprovedBooking = bookingService.readAllBookingsOwner(savedOwner.getId(), WAITING, 0, 10);
        List<BookingDtoResponse> returnedListRejectedBooking = bookingService.readAllBookingsOwner(savedOwner.getId(), REJECTED, 0, 10);

        assertEquals(expectedListApprovedBooking.size(), returnedListApprovedBooking.size());
        assertEquals(expectedListRejectedBooking.size(), returnedListRejectedBooking.size());
        assertEquals(expectedListRejectedBooking.get(0).getStatus(), returnedListRejectedBooking.get(0).getStatus());
    }
}
