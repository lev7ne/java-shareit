package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.AccessDeniedException;
import ru.practicum.shareit.util.exception.ObjectNotFoundException;
import ru.practicum.shareit.util.exception.UnavailableException;
import ru.practicum.shareit.util.exception.UnavailableStateException;
import ru.practicum.shareit.util.validator.ObjectHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingState.ALL;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    User testUser1;
    User testUser2;
    ItemDtoRequest testItemDtoRequest1;
    ItemDtoRequest testItemDtoRequest2;
    Item testItem1;
    Item testItem2;
    Booking testBooking1;
    Booking testBooking2;
    BookingDtoRequest testBookingDtoRequest1;
    PageRequest page;

    @BeforeEach
    void setUp() {
        testUser1 = new User(1, "testUser1", "email1@ya.ru");
        testUser2 = new User(2, "testUser2", "email2@ya.ru");

        testItemDtoRequest1 = new ItemDtoRequest("testItem1", "testDescription1", true, 0);
        testItemDtoRequest2 = new ItemDtoRequest("testItem2", "testDescription2", true, 0);

        testItem1 = new Item(1, "testItem1", "testDescription1", true, testUser1, null);
        testItem2 = new Item(2, "testItem2", "testDescription2", true, testUser2, null);

        LocalDateTime start = LocalDateTime.of(2023, 8, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 8, 21, 10, 0);

        testBookingDtoRequest1 = new BookingDtoRequest(start, end, 1);

        testBooking1 = new Booking(start, end, testItem1, testUser2, WAITING);
        testBooking2 = new Booking(start, end, testItem1, testUser2, WAITING);

        page = ObjectHelper.getPageRequest(0, 10).withSort(Sort.by(Sort.Direction.DESC, "start"));

    }

    @Test
    void add_whenCorrectDto_thenReturnBooking() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockBookingRepository.save(any())).thenReturn(testBooking1);

        BookingDtoResponse createdBooking = bookingService.add(2, testBookingDtoRequest1);

        verify(mockBookingRepository).save(any());

        assertEquals(testBookingDtoRequest1.getStart(), createdBooking.getStart());
        assertEquals(testBookingDtoRequest1.getEnd(), createdBooking.getEnd());
        assertEquals(testBookingDtoRequest1.getItemId(), createdBooking.getItem().getId());
    }

    @Test
    void add_whenIncorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.add(1, testBookingDtoRequest1));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void add_whenIncorrectItemId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(testBookingDtoRequest1.getItemId())).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.add(testBookingDtoRequest1.getItemId(), testBookingDtoRequest1));

        assertEquals("Предмет id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void add_whenBookerIdEqualsItemOwnerId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.add(1, testBookingDtoRequest1));

        assertEquals("Нельзя забронировать свой предмет.", objectNotFoundException.getMessage());
    }

    @Test
    void add_whenItemUnavailable_thenItemAvailabilityExceptionThrown() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        testItem1.setAvailable(false);
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));

        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> bookingService.add(2, testBookingDtoRequest1));

        assertEquals(testItem1.getName() + " - забронирован другим пользователем.",
                unavailableException.getMessage());
    }

    @Test
    void update_whenUserNotFound_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.update(1, 2, true));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void update_whenBookingStatusApproved_thanThrowUnavailableException() {
        testBooking1.setBookingStatus(APPROVED);

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));

        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> bookingService.update(1, 1, true));

        assertEquals("Запрос на бронирование уже был обработан ранее.", unavailableException.getMessage());
    }

    @Test
    void update_whenBookingAvailableFalse_thanThrowUnavailableException() {
        testBooking1.getItem().setAvailable(false);

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));

        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> bookingService.update(1, 1, true));

        assertEquals(testBooking1.getItem().getName() + " забронирован ранее.", unavailableException.getMessage());
    }

    @Test
    void update_whenBookingNotFound_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.update(2, 1, true));

        assertEquals("Бронирование id: " + 1 + " не найдено или ещё не создано.", objectNotFoundException.getMessage());
    }

    @Test
    void update_whenBookingAvailable_thenApprovedBooking() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));

        testBooking2.setBookingStatus(APPROVED);

        when(mockBookingRepository.save(Mockito.any())).thenReturn(testBooking2);

        BookingDtoResponse updatedBooking = bookingService.update(1, 1, true);

        assertEquals(testBooking2.getBookingStatus(), updatedBooking.getStatus());
    }

    @Test
    void find_whenFieldsCorrect_thenReturnedBooking() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));

        BookingDtoResponse returnBooking = bookingService.find(1, 1);

        verify(mockBookingRepository).findById(1L);

        assertEquals(testBooking1.getStart(), returnBooking.getStart());
        assertEquals(testBooking1.getEnd(), returnBooking.getEnd());
        assertEquals(testBooking1.getItem().getId(), returnBooking.getItem().getId());
    }

    @Test
    void find_whenUserNotFound_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.find(1, 1));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void find_whenBookingNotFound_thenThrowObjectNotFoundException() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.find(2, 1));

        assertEquals("Бронирование id: " + 1 + " не найдено или ещё не создано.", objectNotFoundException.getMessage());
    }

    @Test
    void find_whenUserNotOwner_whenThrowAccessDeniedException() {
        User testUser3 = new User(3, "testUser3", "email3@ya.ru");

        when(mockUserRepository.findById(3L)).thenReturn(Optional.of(testUser3));
        when(mockBookingRepository.findById(1L)).thenReturn(Optional.of(testBooking2));

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> bookingService.find(3, 1));

        assertEquals("Просмотр разрешен только владельцу предмета или бронирования.", accessDeniedException.getMessage());
    }

    @Test
    void readAllBookingsBooker_whenIncorrectUserId_thenThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.find(1, 1));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void readAllBookingsBooker_whenAllState_thenReturnAllBookings() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockBookingRepository.findAllByBookerId(2, page)).thenReturn(List.of(testBooking1, testBooking2));

        List<BookingDtoResponse> returnedBookingsList = new ArrayList<>(bookingService
                .readAllBookingsBooker(2, ALL, 0, 10));

        Mockito.verify(mockBookingRepository).findAllByBookerId(2, page);

        assertEquals(2, returnedBookingsList.size());
    }

    @Test
    void readAllBookingsBooker_whenIncorrectState_thenReturnAllBookings() {

        UnavailableStateException unavailableStateException = assertThrows(UnavailableStateException.class,
                () -> bookingService.readAllBookingsBooker(1, BookingState.valueOf("INCORRECT"), 0, 10));

        assertEquals("Unknown state: INCORRECT", unavailableStateException.getMessage());
    }

    @Test
    void readAllBookingsOwner_whenIncorrectState_thenReturnAllBookings() {

        UnavailableStateException unavailableStateException = assertThrows(UnavailableStateException.class,
                () -> bookingService.readAllBookingsBooker(1, BookingState.valueOf("INCORRECT"), 0, 10));

        assertEquals("Unknown state: INCORRECT", unavailableStateException.getMessage());
    }


}