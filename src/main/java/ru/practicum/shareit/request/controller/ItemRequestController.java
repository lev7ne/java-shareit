package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDtoResponse createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDtoRequest itemRequestDtoRequest) {
        return itemRequestService.add(userId, itemRequestDtoRequest);
    }

    @GetMapping
    public List<ItemRequestDtoResponse> getRequests(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestService.findAllByUserId(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        return itemRequestService.find(userId, requestId);
    }

}
