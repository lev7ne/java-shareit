package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exception.ObjectNotFoundException;
import ru.practicum.shareit.util.exception.UnavailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    User testUser1;
    User testUser2;
    Item testItem1;
    Item testItem2;
    ItemRequestDtoRequest testItemRequestDtoRequest1;
    ItemRequestDtoRequest testItemRequestDtoRequest2;
    ItemRequest testItemRequest1;
    ItemRequest testItemRequest2;

    @BeforeEach
    void setUp() {

        testUser1 = new User(1, "testUser1", "email1@ya.ru");
        testUser2 = new User(2, "testUser2", "email2@ya.ru");


        LocalDateTime dateTime = LocalDateTime.of(2024, 8, 10, 10, 0);

        testItemRequest1 = new ItemRequest(1, "testItemRequestDescription1", testUser2, dateTime.plusHours(1));
        testItemRequest2 = new ItemRequest(2, "testItemRequestDescription2", testUser2, dateTime.plusHours(2));

        testItem1 = new Item(1, "testItem1", "testDescription1", true, testUser1, testItemRequest1);
        testItem2 = new Item(2, "testItem1", "testDescription1", true, testUser1, testItemRequest1);

        testItemRequestDtoRequest1 = new ItemRequestDtoRequest("testItemRequestDescription1");
        testItemRequestDtoRequest2 = new ItemRequestDtoRequest("testItemRequestDescription2");
    }

    @Test
    void add_whenCorrectUserIdAndRequestFields_thenAddRequest() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockItemRequestRepository.save(any())).thenReturn(testItemRequest1);

        ItemRequestDtoResponse createdRequest = itemRequestService.add(2, testItemRequestDtoRequest1);

        verify(mockItemRequestRepository).save(any());

        assertEquals(testItemRequestDtoRequest1.getDescription(), createdRequest.getDescription());
    }

    @Test
    void findByRequesterId_whenIncorrectUserId_thanThrowObjectNotFoundException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.empty());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.add(1, any()));

        assertEquals("Пользователь id: " + 1 + " не найден или ещё не создан.", objectNotFoundException.getMessage());
    }

    @Test
    void findAllByRequesterId_whenIncorrectFromOrSizePageable_thanThrowUnavailableException() {
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        UnavailableException unavailableException = assertThrows(UnavailableException.class,
                () -> itemRequestService.findAllByRequesterId(1, -1, -1));
        assertEquals("Некорректное значение from или size.", unavailableException.getMessage());
    }

    @Test
    void findByRequesterId_whenAllCorrect_thenReturnRequests() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockItemRequestRepository.findAllByRequesterId(2))
                .thenReturn(List.of(testItemRequest1, testItemRequest2));
        when(mockItemRepository.getItemsByItemRequest_IdIn(List.of(1L, 2L))).thenReturn(List.of(testItem1, testItem2));

        List<ItemRequestDtoResponse> requests = itemRequestService.findByRequesterId(2);

        assertEquals(2, requests.size());
    }

    @Test
    void findByRequesterId_whenAllCorrectButRequestDidNotCreate_thenReturnEmptyList() {
        when(mockUserRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(mockItemRequestRepository.findAllByRequesterId(2))
                .thenReturn(List.of());

        List<ItemRequestDtoResponse> requests = itemRequestService.findByRequesterId(2);

        assertEquals(0, requests.size());
    }
}