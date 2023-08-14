package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.AccessDeniedException;
import ru.practicum.shareit.util.exception.ObjectNotFoundException;
import ru.practicum.shareit.util.exception.UnavailableException;
import ru.practicum.shareit.util.validator.ObjectHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    User testUser1;
    ItemDtoRequest testItemDtoRequest1;
    Item testItem1;
    ItemDtoResponse testItemDtoResponse1;
    ItemRequest testItemRequest1;
    CommentDto testCommentDto1;

    @BeforeEach
    void setUp() {
        testUser1 = new User(1, "testUser1", "testDescription1");
        testItemDtoRequest1 = new ItemDtoRequest("testItem1", "testDescription1", true, 0);
        testItem1 = new Item(1, "testItem1", "testDescription1", true, testUser1, null);
        testItemRequest1 = new ItemRequest("testRequest1", testUser1, LocalDateTime.now());
        testCommentDto1 = new CommentDto("textTestComment");
    }

    @Test
    void save_whenItemDtoRequestWithoutRequestId_thanSavedCorrect() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.save(any())).thenReturn(testItem1);

        testItemDtoResponse1 = itemService.save(1, testItemDtoRequest1);

        assertEquals(testItemDtoRequest1.getName(), testItemDtoResponse1.getName());
        assertEquals(testItemDtoRequest1.getDescription(), testItemDtoResponse1.getDescription());
        assertEquals(testItemDtoRequest1.getAvailable(), testItemDtoResponse1.getAvailable());
    }

    @Test
    void save_whenIncorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.save(1, any()));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void save_whenUserIdEqualsRequesterOwnerId_thanThrowUnavailableException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        testItemDtoRequest1.setRequestId(1);
        when(mockItemRequestRepository.findById(testItemDtoRequest1.getRequestId())).thenReturn(Optional.of(testItemRequest1));

        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> itemService.save(testUser1.getId(), testItemDtoRequest1));

        assertEquals("Пользователь не может создать вещь в ответ на свой запрос.", unavailableException.getMessage());
    }

    @Test
    void update_whenIncorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(1, testItemDtoRequest1, 1));
        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void update_whenIncorrectItemId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(1, testItemDtoRequest1, 1));
        assertEquals("Предмет id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void update_whenUserIdIsNotItemOwner_thanThrowObjectNotFoundException() {
        User anyUser = new User(2, "testUser1", "testDescription1");
        testItem1.setOwner(anyUser);
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class,
                () -> itemService.update(1, testItemDtoRequest1, 1));
        assertEquals("Только владельцу предмета разрешено редактирование.", accessDeniedException.getMessage());
    }

    @Test
    void update_whenItemDtoWithCorrectFields_thanSavedCorrect() {
        ItemDtoRequest updatedItem = new ItemDtoRequest(1, "updateItem1", "updateDescription1", false, 0);

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockItemRepository.save(any())).thenReturn(ItemDtoMapper.mapToItem(updatedItem));

        testItemDtoResponse1 = itemService.update(1, updatedItem, 1);

        assertEquals(testItemDtoResponse1.getName(), updatedItem.getName());
        assertEquals(testItemDtoResponse1.getDescription(), updatedItem.getDescription());
        assertEquals(testItemDtoResponse1.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void find_whenIncorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.find(1, 1));
        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());

    }

    @Test
    void find_whenIncorrectItemId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.find(1, 1));
        assertEquals("Предмет id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void find_whenCorrectItemIdAndUserIdAndEmptyComment_thanReturnItemWithEmptyCommentList() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        testUser1.setId(2);
        testItem1.setOwner(testUser1);
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockCommentRepository.findAllByItemId(testItem1.getId())).thenReturn(List.of());

        ItemDtoResponse itemDtoResponse = itemService.find(1, 1);

        assertEquals(itemDtoResponse.getComments(), List.of());
    }

    @Test
    void find_whenCorrectItemIdAndUserIdAndEmptyComment_thanReturnItemWithCommentListAndBookings() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 10, 0);
        Booking lastBooking = new Booking(start, end, testItem1, new User(), APPROVED);
        Booking nextBooking = new Booking(start.plusYears(1), end.plusYears(1), testItem1, new User(), APPROVED);

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        testItem1.setOwner(testUser1);
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockBookingRepository.findLastApprovedBookingByItemId(anyLong(), any(), any(), any())).thenReturn(List.of(lastBooking));
        when(mockBookingRepository.findNextApprovedBookingByItemId(anyLong(), any(), any(), any())).thenReturn(List.of(nextBooking));
        when(mockCommentRepository.findAllByItemId(testItem1.getId())).thenReturn(List.of());

        ItemDtoResponse itemDtoResponse = itemService.find(1, 1);

        assertEquals(itemDtoResponse.getComments(), List.of());
        assertEquals(itemDtoResponse.getLastBooking(), BookingDtoMapper.toBookingDtoResponseShort(lastBooking));
        assertEquals(itemDtoResponse.getNextBooking(), BookingDtoMapper.toBookingDtoResponseShort(nextBooking));
    }

    @Test
    void find_whenCorrectItemIdAndUserIdAndEmptyComment_thanReturnItemWithEmptyCommentListAndEmptyBookings() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        testItem1.setOwner(testUser1);
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockBookingRepository.findLastApprovedBookingByItemId(anyLong(), any(), any(), any())).thenReturn(List.of());
        when(mockBookingRepository.findNextApprovedBookingByItemId(anyLong(), any(), any(), any())).thenReturn(List.of());
        when(mockCommentRepository.findAllByItemId(testItem1.getId())).thenReturn(List.of());

        ItemDtoResponse itemDtoResponse = itemService.find(1, 1);

        assertEquals(itemDtoResponse.getComments(), List.of());
        assertNull(itemDtoResponse.getLastBooking());
        assertNull(itemDtoResponse.getNextBooking());
    }

    @Test
    void getAll_whenIncorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getAll(1, 0, 10));
        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void getAll_whenCorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        PageRequest page = ObjectHelper.getPageRequest(0, 10);
        Item testItem2 = new Item(2, "testItem2", "testDescription2", true, testUser1, null);

        when(mockItemRepository.getItemsByOwner_Id(1, page)).thenReturn(List.of(testItem1, testItem2));

        List<ItemDtoResponse> itemDtoResponseList = itemService.getAll(1, 0, 10);

        assertEquals(itemDtoResponseList.size(), 2);
        assertEquals(itemDtoResponseList.get(1), ItemDtoMapper.mapToItemDtoResponse(testItem2));
    }

    @Test
    void search_whenTextIsEmpty_thenReturnedEmptyCollection() {
        List<ItemDtoResponse> returnedItems = new ArrayList<>(itemService.search(" ", 0, 10));

        assertEquals(0, returnedItems.size());
    }

    @Test
    void search_whenItemIncludeText_thenListWithItem() {
        PageRequest page = ObjectHelper.getPageRequest(0, 10);
        String description = "Description";
        when(mockItemRepository.search(description, page)).thenReturn(List.of(testItem1));

        List<ItemDtoResponse> itemDtoResponseList = itemService.search(description, 0, 10);

        assertNotNull(itemDtoResponseList);
    }

    @Test
    void createComment_whenUserIdIncorrect_thenThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.saveComment(1, testCommentDto1, 1));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void createComment_whenItemNotFound_thenThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.saveComment(1, testCommentDto1, 1));

        assertEquals("Предмет id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void saveComment_whenUserDidNotBookItem_thenThrowUnavailableException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockBookingRepository.findAllByItem_IdAndBooker_IdAndBookingStatus(1, 1, APPROVED)).thenReturn(List.of());

        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> itemService.saveComment(1, testCommentDto1, 1));

        assertEquals(("Пользователь с id: " + 1 + " не бронировал вещь с id: " + 1), unavailableException.getMessage());
    }

    @Test
    void saveComment_whenAllCorrect_thenReturnComment() {
        User testUser2 = new User(2, "testUser2", "testDescription2");

        LocalDateTime start = LocalDateTime.of(2023, 8, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 9, 1, 10, 0);

        Booking booking = new Booking(start, end, testItem1, testUser2, APPROVED);
        Comment testComment = new Comment(1, "textTestComment", testItem1, testUser2, LocalDateTime.now());

        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockBookingRepository.findAllByItem_IdAndBooker_IdAndBookingStatus(1L, 2L, APPROVED)).thenReturn(List.of(booking));
        when(mockCommentRepository.save(any())).thenReturn(testComment);

        CommentDto createdComment = itemService.saveComment(2L, testCommentDto1, 1L);

        assertEquals(createdComment.getText(), testCommentDto1.getText());
    }

    @Test
    void saveComment_whenBookingNotOver_thenThrowUnavailableException() {
        User testUser2 = new User(2, "testUser2", "testDescription2");

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);
        Booking booking = new Booking(start, end, testItem1, testUser2, APPROVED);

        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockItemRepository.findById(1L)).thenReturn(Optional.of(testItem1));
        when(mockBookingRepository.findAllByItem_IdAndBooker_IdAndBookingStatus(1L, 2L, APPROVED))
                .thenReturn(List.of(booking));


        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> itemService.saveComment(2, testCommentDto1, 1));

        assertEquals("Отзыв можно оставить только после состоявшегося бронирования.",
                unavailableException.getMessage());
    }
}