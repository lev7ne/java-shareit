package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService mockItemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    ItemRequestDtoRequest itemRequestDtoRequest1;
    ItemRequestDtoRequest itemRequestDtoRequest2;
    ItemRequestDtoResponse itemRequestDtoResponse1;
    ItemRequestDtoResponse itemRequestDtoResponse2;

    @BeforeEach
    public void setUp() {
        itemRequestDtoRequest1 = new ItemRequestDtoRequest("description1");
        itemRequestDtoRequest2 = new ItemRequestDtoRequest("description2");

        itemRequestDtoResponse1 = new ItemRequestDtoResponse(1, "description1", null, null);
        itemRequestDtoResponse2 = new ItemRequestDtoResponse(2, "description2", null, null);
    }

    @Test
    void createRequest() throws Exception {
        when(mockItemRequestService.add(anyLong(), any())).thenReturn(itemRequestDtoResponse1);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDtoRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemRequestDtoRequest1.getDescription())));
    }

    @Test
    void findRequestsByRequesterId() throws Exception {
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = List.of(itemRequestDtoResponse1, itemRequestDtoResponse2);

        when(mockItemRequestService.findByRequesterId(anyLong()))
                .thenReturn(itemRequestDtoResponseList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoRequest1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is(itemRequestDtoRequest2.getDescription())));
    }

    @Test
    void findRequestByUserIdAndRequestId() throws Exception {
        when(mockItemRequestService.findRequestByUserIdAndRequestId(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoResponse1);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemRequestDtoRequest1.getDescription())));
    }

    @Test
    void getRequests() throws Exception {
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = List.of(itemRequestDtoResponse1, itemRequestDtoResponse2);

        when(mockItemRequestService.findAllByRequesterId(eq(1L), eq(0), eq(10)))
                .thenReturn(itemRequestDtoResponseList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoRequest1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is(itemRequestDtoRequest2.getDescription())));
    }
}
