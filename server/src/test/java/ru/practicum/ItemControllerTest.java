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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.item.ItemController;
import ru.practicum.item.dto.*;
import ru.practicum.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemResponseDto itemResponseDto;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("book")
                .description("nice book")
                .available(true)
                .comments(Collections.emptyList())
                .build();

        itemCreateDto = new ItemCreateDto("book", "nice book", true, null);
        itemUpdateDto = new ItemUpdateDto("new book", null, null);

        commentRequestDto = new CommentRequestDto("great");
        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("great")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addItem_shouldReturnCreatedItem() throws Exception {
        Mockito.when(itemService.addItem(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ItemCreateDto.class))).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("book")));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        itemResponseDto.setName("new book");
        Mockito.when(itemService.updateItem(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(ItemUpdateDto.class))).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("new book")));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        Mockito.when(itemService.getItemById(1L, 1L)).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)));
    }

    @Test
    void getItemsByOwner_shouldReturnListOfItems() throws Exception {
        Mockito.when(itemService.getItemsByOwner(1L, 0, 10)).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)));
    }

    @Test
    void searchAvailableItems_shouldReturnListOfItems() throws Exception {
        Mockito.when(itemService.searchAvailableItems("search text", 0, 10)).thenReturn(List.of(itemResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "search text")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        Mockito.when(itemService.addComment(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header(USER_ID_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Matchers.is("great")));
    }
}