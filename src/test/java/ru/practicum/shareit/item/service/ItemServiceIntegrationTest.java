package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {
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
    BookingDtoRequest bookingDtoRequestLast;
    BookingDtoRequest bookingDtoRequestNext;
    CommentDto commentDto;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto("user1", "email1@ya.ru");
        userDto2 = new UserDto("user2", "email2@ya.ru");

        itemDtoRequest1 = new ItemDtoRequest("Item1", "Item1 Description", true, 0);
        itemDtoRequest2 = new ItemDtoRequest("Item2", "Item2 Description", true, 0);

        LocalDateTime startLast = LocalDateTime.of(2023, 8, 1, 10, 0);
        LocalDateTime endLast = LocalDateTime.of(2023, 8, 2, 10, 0);

        LocalDateTime startNext = LocalDateTime.now().plusHours(1);
        LocalDateTime endNext = LocalDateTime.now().plusHours(2);

        bookingDtoRequestLast = new BookingDtoRequest(startLast, endLast, 1);
        bookingDtoRequestNext = new BookingDtoRequest(startNext, endNext, 1);

        commentDto = new CommentDto("comment");
    }

    @Test
    void getAll_thenReturnedItemCollection() {
        UserDto savedOwner = userService.save(userDto1);
        UserDto savedBooker = userService.save(userDto2);
        ItemDtoResponse savedItem1 = itemService.save(savedOwner.getId(), itemDtoRequest1);
        ItemDtoResponse savedItem2 = itemService.save(savedOwner.getId(), itemDtoRequest2);
        BookingDtoResponse savedLastBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestLast);
        BookingDtoResponse savedNextBooking = bookingService.add(savedBooker.getId(), bookingDtoRequestNext);

        bookingService.update(savedOwner.getId(), savedLastBooking.getId(), true);
        bookingService.update(savedOwner.getId(), savedNextBooking.getId(), true);

        CommentDto savedComment = itemService.saveComment(savedBooker.getId(), commentDto, savedItem1.getId());

        List<ItemDtoResponse> expectedItems = List.of(savedItem1, savedItem2);

        List<ItemDtoResponse> returnedItems = itemService.getAll(savedOwner.getId(), 0, 10);

        assertEquals(expectedItems.size(), returnedItems.size());

        assertEquals(expectedItems.get(0).getId(), returnedItems.get(0).getId());
        assertEquals(expectedItems.get(0).getName(), returnedItems.get(0).getName());
        assertEquals(expectedItems.get(0).getDescription(), returnedItems.get(0).getDescription());
        assertEquals(expectedItems.get(0).getAvailable(), returnedItems.get(0).getAvailable());
        assertEquals(savedComment.getText(), returnedItems.get(0).getComments().get(0).getText());
        assertEquals(1, returnedItems.get(0).getLastBooking().getId());
        assertEquals(2, returnedItems.get(0).getLastBooking().getBookerId());
        assertEquals(2, returnedItems.get(0).getNextBooking().getId());
        assertEquals(2, returnedItems.get(0).getNextBooking().getBookerId());
        assertEquals(expectedItems.get(1).getId(), returnedItems.get(1).getId());
        assertEquals(expectedItems.get(1).getName(), returnedItems.get(1).getName());
        assertEquals(expectedItems.get(1).getDescription(), returnedItems.get(1).getDescription());
        assertEquals(expectedItems.get(1).getAvailable(), returnedItems.get(1).getAvailable());
        assertNull(returnedItems.get(1).getLastBooking());
        assertNull(returnedItems.get(1).getNextBooking());
    }
}
