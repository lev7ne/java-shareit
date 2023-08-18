package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService mockUserService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto1;
    private UserDto userDto2;


    @BeforeEach
    public void setUp() {
        userDto1 = new UserDto(1, "user1", "user1@ya.ru");
        userDto2 = new UserDto(2, "user2", "user2@ya.ru");
    }

    @Test
    public void createUser_ReturnsCreatedUser() throws Exception {
        when(mockUserService.save(userDto1)).thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    public void updateUser_ReturnsUpdatedUser() throws Exception {

        when(mockUserService.update(anyLong(), any()))
                .thenReturn(userDto1);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    public void getUserById_ReturnsUser() throws Exception {

        when(mockUserService.find(eq(1L))).thenReturn(userDto1);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("user1")))
                .andExpect(jsonPath("$.email", is("user1@ya.ru")));
    }

    @Test
    public void deleteUser_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllUsers_ReturnsAllUsers() throws Exception {
        List<UserDto> userList = Arrays.asList(userDto1, userDto2);
        when(mockUserService.getAll()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("user1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("user2")));
    }
}
