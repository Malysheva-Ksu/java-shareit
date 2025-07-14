package ru.practicum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.request.ItemRequestController;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private ItemRequestResponseDto responseDto;
    private ItemRequestCreateDto createDto;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        responseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("need book")
                .createdAt(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        createDto = new ItemRequestCreateDto("need book");
    }

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        Mockito.when(requestService.create(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ItemRequestCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("need book")));
    }

    @Test
    void findOwn_shouldReturnListOfRequests() throws Exception {
        Mockito.when(requestService.findOwn(1L, 1, 100))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "1")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(responseDto.getId().intValue())));
    }

    @Test
    void findAll_shouldReturnListOfRequests() throws Exception {
        Mockito.when(requestService.findAll(1L, 0, 10)).thenReturn(List.of(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    void findById_shouldReturnRequest() throws Exception {
        Mockito.when(requestService.findById(1L, 1L)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)));
    }
}