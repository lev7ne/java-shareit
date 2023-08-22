package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.validator.ObjectHelper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
class ItemRepositoryTest {

    User testUser;
    UserDto testUserDto;
    Item testItem;
    ItemDtoRequest testItemDtoRequest;
    ItemRequest testItemRequest;
    PageRequest testPage;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto("name", "email@ya.ru");
        testUser = UserDtoMapper.toUser(testUserDto);
        testUser = userRepository.save(testUser);


        testItemDtoRequest = new ItemDtoRequest("itemName", "description", true, testUser.getId());
        testItem = ItemDtoMapper.mapToItem(testItemDtoRequest);
        testItem.setOwner(testUser);

        testPage = ObjectHelper.getPageRequest(0, 10);

        testItemRequest = new ItemRequest("testDescription", testUser, LocalDateTime.now());
    }

    @Test
    void getItemsByItemRequest_IdIn_whenIdsListIsCorrectValues_thenReturnListItems() {
        testItemRequest = itemRequestRepository.save(testItemRequest);
        testItem.setItemRequest(testItemRequest);
        itemRepository.save(testItem);
        List<Long> requestIdsList = List.of(testItem.getItemRequest().getId());
        List<Item> returnedItems = itemRepository.getItemsByItemRequest_IdIn(requestIdsList);
        assertEquals(returnedItems.get(0).getId(), testItem.getId());
    }

    @Test
    void save_whenValidItem_thenSaveItem() {
        assertEquals(testItem.getId(), 0);
        Item returnedItem = itemRepository.save(testItem);
        assertEquals(returnedItem.getId(), 1);
    }

    @Test
    void search_whenBdIsNotEmpty_thenReturnListWithTestItem() {
        List<Item> items = itemRepository.search("description", testPage);
        assertEquals(items, List.of());
        itemRepository.save(testItem);
        items = itemRepository.search("description", testPage);
        assertEquals(items, List.of(testItem));
    }

    @Test
    void getItemsByOwner_Id_whenIdIsCorrect_thenReturnListItems() {
        itemRepository.save(testItem);
        List<Item> returnedItems = itemRepository.getItemsByOwner_Id(testUser.getId(), testPage);
        assertEquals(returnedItems.get(0).getOwner().getId(), testUser.getId());
    }

    @Test
    void getItemsByOwner_Id_whenIdIsIncorrect_thenReturnEmptyList() {
        itemRepository.save(testItem);
        List<Item> returnedItems = itemRepository.getItemsByOwner_Id(2, testPage);
        assertEquals(returnedItems, List.of());
    }

    @Test
    void getItemByItemRequest_Id_whenIdIsCorrect_thenReturnedItem() {
        testItemRequest = itemRequestRepository.save(testItemRequest);
        testItem.setItemRequest(testItemRequest);
        itemRepository.save(testItem);
        Item returnedItem = itemRepository.getItemByItemRequest_Id(testItemRequest.getId());
        assertEquals(returnedItem, testItem);
    }
}