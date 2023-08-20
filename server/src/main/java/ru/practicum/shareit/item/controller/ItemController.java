package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @RequestBody ItemDtoRequest itemDtoRequest) {
        return itemService.save(ownerId, itemDtoRequest);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @RequestBody ItemDtoRequest itemDtoRequest, @PathVariable("id") long itemId) {
        return itemService.update(ownerId, itemDtoRequest, itemId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItem(@RequestParam("text") String text,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        return itemService.search(text, from, size);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getItem(@PathVariable long id,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.find(id, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return itemService.getAll(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId) {
        return itemService.saveComment(bookerId, commentDto, itemId);
    }
}
