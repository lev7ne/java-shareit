package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.validator.ObjectHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestDtoResponse add(long userId, ItemRequestDtoRequest itemRequestDtoRequest) {
        User user = ObjectHelper.findUserById(userRepository, userId);
        ItemRequest itemRequest = ItemRequestDtoMapper.toItemRequest(itemRequestDtoRequest, user, LocalDateTime.now());
        return ItemRequestDtoMapper.toItemRequestDtoResponse(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> findByRequesterId(long requesterId) {

        ObjectHelper.findUserById(userRepository, requesterId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterId(requesterId);

        return findAllItemsForItemRequestDtoResponse(itemRequestList);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoResponse findRequestByUserIdAndRequestId(long userId, long requestId) {
        ObjectHelper.findUserById(userRepository, userId);

        ItemRequest itemRequest = ObjectHelper.findItemRequestById(itemRequestRepository, requestId);

        Item item = itemRepository.getItemByItemRequest_Id(requestId);

        List<ItemDtoResponse> itemDtoResponseList = new ArrayList<>();
        if (item != null) {
            itemDtoResponseList.add(ItemDtoMapper.mapToItemDtoResponse(item));
        }

        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestDtoMapper.toItemRequestDtoResponse(itemRequest);
        itemRequestDtoResponse.setItems(itemDtoResponseList);

        return itemRequestDtoResponse;
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoResponse> findAllByRequesterId(long requesterId, Integer from, Integer size) {
        ObjectHelper.findUserById(userRepository, requesterId);
        Pageable page = ObjectHelper.getPageRequest(from, size);

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterId(requesterId, page);

        return findAllItemsForItemRequestDtoResponse(itemRequestList);
    }

    private List<ItemRequestDtoResponse> findAllItemsForItemRequestDtoResponse(List<ItemRequest> itemRequestList) {

        if (itemRequestList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ItemRequestDtoResponse> itemRequestDtoResponseList = itemRequestList.stream()
                .map(ItemRequestDtoMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());

        List<Long> requestIdsList = itemRequestDtoResponseList.stream()
                .map(ItemRequestDtoResponse::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.getItemsByItemRequest_IdIn(requestIdsList);

        Map<Long, List<Item>> itemDtoResponseMap = items.stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        itemRequestDtoResponseList.forEach(itemRequestDtoResponse ->
                itemRequestDtoResponse.setItems(itemDtoResponseMap.getOrDefault(itemRequestDtoResponse.getId(), new ArrayList<>()).stream()
                        .map(ItemDtoMapper::mapToItemDtoResponse)
                        .collect(Collectors.toList())));

        return itemRequestDtoResponseList;
    }
}
