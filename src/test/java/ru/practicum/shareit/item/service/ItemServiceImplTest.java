package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

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


    @BeforeEach
    void setUp() {

    }


}