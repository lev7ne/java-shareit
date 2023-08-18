package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService mockItemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    ItemDtoRequest itemDtoRequest1;
    ItemDtoRequest itemDtoRequest2;
    ItemDtoResponse itemDtoResponse1;
    ItemDtoResponse itemDtoResponse2;
    CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDtoRequest1 = new ItemDtoRequest("itemName1", "itemDescription1", true, 1);
        itemDtoRequest2 = new ItemDtoRequest("itemName2", "itemDescription2", true, 1);

        itemDtoResponse1 = new ItemDtoResponse(1, "itemName1", "itemDescription1", true);
        itemDtoResponse2 = new ItemDtoResponse(2, "itemName2", "itemDescription2", true);
    }

    @Test
    public void createItem_whenCorrectUserIdAndCorrectDto_thanReturnsCreatedItem() throws Exception {
        when(mockItemService.save(anyLong(), any())).thenReturn(itemDtoResponse1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoRequest1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoRequest1.getDescription())));
    }

    @Test
    void updateItem_whenCorrectUserIdAndCorrectDto_thanReturnsUpdatedItem() throws Exception {
        when(mockItemService.update(anyLong(), any(), anyLong())).thenReturn(itemDtoResponse1);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoResponse1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoResponse1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse1.getDescription())));
    }

    @Test
    void searchItem() throws Exception {
        List<ItemDtoResponse> itemDtoResponseList = Arrays.asList(itemDtoResponse1, itemDtoResponse2);
        when(mockItemService.search(eq("Description"), eq(0), eq(10))).thenReturn(itemDtoResponseList);

        mockMvc.perform(get("/items/search")
                        .queryParam("text", "Description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDtoRequest1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoRequest1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is(itemDtoRequest2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoRequest2.getDescription())));
    }

    @Test
    void getItem_whenCorrectItemIdAndUserId_thanReturnCorrectItem() throws Exception {
        when(mockItemService.find(anyLong(), anyLong()))
                .thenReturn(itemDtoResponse1);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(itemDtoRequest1.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoRequest1.getDescription())));
    }

    @Test
    void getAllItems() throws Exception {
        List<ItemDtoResponse> itemDtoResponseList = Arrays.asList(itemDtoResponse1, itemDtoResponse2);
        when(mockItemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemDtoResponseList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDtoRequest1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoRequest1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is(itemDtoRequest2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDtoRequest2.getDescription())));
    }

    @Test
    void createComment() throws Exception {
        commentDto = new CommentDto("testCommentText");

        when(mockItemService.saveComment(anyLong(), any(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}