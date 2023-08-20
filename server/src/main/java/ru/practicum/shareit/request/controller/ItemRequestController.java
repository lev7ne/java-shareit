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
    public List<ItemRequestDtoResponse> findRequestsByRequesterId(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestService.findByRequesterId(requesterId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse findRequestByUserIdAndRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                  @PathVariable long requestId) {
        return itemRequestService.findRequestByUserIdAndRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getRequests(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.findAllByRequesterId(requesterId, from, size);
    }
}
