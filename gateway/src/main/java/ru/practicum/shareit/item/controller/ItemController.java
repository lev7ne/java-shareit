package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @Valid @RequestBody ItemDtoRequest itemDtoRequest) {
        return itemClient.save(ownerId, itemDtoRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestBody ItemDtoRequest itemDtoRequest, @PathVariable("id") long itemId) {
        return itemClient.update(ownerId, itemDtoRequest, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam("text") String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        if (text.isBlank()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return itemClient.search(ownerId, text, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable long id,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.find(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAll(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable long itemId) {
        return itemClient.createComment(bookerId, commentDto, itemId);
    }
}
