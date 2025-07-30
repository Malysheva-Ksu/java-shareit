package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.user.UserController;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserUpdateDto;
import ru.practicum.user.service.UserService;

import java.util.List;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponseDto userResponseDto;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("user")
                .email("test@example.com")
                .build();

        userCreateDto = new UserCreateDto("user", "test@example.com");

        userUpdateDto = new UserUpdateDto("Updated User", "updated@example.com");
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        Page<UserResponseDto> userPage = new PageImpl<>(List.of(userResponseDto));
        Mockito.when(userService.getAllUsers(ArgumentMatchers.any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("user")));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(userResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("test@example.com")));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        Mockito.when(userService.createUser(ArgumentMatchers.any(UserCreateDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserResponseDto updatedResponse = UserResponseDto.builder()
                .id(1L)
                .name("Updated User")
                .email("updated@example.com")
                .build();
        Mockito.when(userService.updateUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserUpdateDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("Updated User")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("updated@example.com")));
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(1L);
    }
}