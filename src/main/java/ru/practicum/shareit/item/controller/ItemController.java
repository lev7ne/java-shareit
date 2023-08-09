package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
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
                                      @Valid @RequestBody ItemDtoRequest itemDto) {
        return itemService.save(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @RequestBody ItemDtoRequest itemDto, @PathVariable("id") long itemId) {
        return itemService.update(ownerId, itemDto, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDtoResponse> searchItem(@RequestParam("text") String text) {
        return itemService.search(text);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getItem(@PathVariable long id,
                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.find(id, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAll(ownerId);
    }

    @GetMapping("/all")
    public List<ItemDtoResponse> getAll(@RequestHeader("X-Sharer-User-Id")
                                        @RequestParam String anyParam,
                                        long ownerId) {
        return itemService.getAll(ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable long itemId) {
        return itemService.saveComment(bookerId, commentDto, itemId);
    }
}
