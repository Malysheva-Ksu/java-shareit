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
import ru.practicum.booking.BookingController;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private BookingResponseDto bookingResponseDto;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .item(new BookingResponseDto.ItemDto(10L, "book"))
                .booker(new BookingResponseDto.BookerDto(2L, "name"))
                .build();

        bookingRequestDto = new BookingRequestDto(10L, start, end);
    }

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        Mockito.when(bookingService.createBooking(ArgumentMatchers.anyLong(), ArgumentMatchers.any(BookingRequestDto.class)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header(USER_ID_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("WAITING")));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingService.approveBooking(1L, 1L, true))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("APPROVED")));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(USER_ID_HEADER, 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.name", Matchers.is("book")));
    }

    @Test
    void getBookingsForUser_shouldReturnListOfBookings() throws Exception {
        Mockito.when(bookingService.getBookingsForUser(2L, BookingState.ALL, 0, 10))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(USER_ID_HEADER, 2L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)));
    }

    @Test
    void getBookingsForOwner_shouldReturnListOfBookings() throws Exception {
        Mockito.when(bookingService.getBookingsForOwner(1L, BookingState.ALL, 0, 10))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.name", Matchers.is("book")));
    }
}