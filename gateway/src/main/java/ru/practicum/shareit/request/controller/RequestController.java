package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDtoRequest itemRequestDtoRequest) {
        return itemRequestClient.add(userId, itemRequestDtoRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestsByRequesterId(@RequestHeader("X-Sharer-User-Id") long requesterId) {
        return itemRequestClient.findByRequesterId(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestByUserIdAndRequestId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                  @PathVariable long requestId) {
        return itemRequestClient.findRequestByUserIdAndRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long requesterId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.findAllByRequesterId(requesterId, from, size);
    }
}