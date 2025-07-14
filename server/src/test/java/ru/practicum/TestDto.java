package ru.practicum;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.NearestBookingDto;
import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemResponseDto;
import ru.practicum.request.dto.ItemRequestCreateDto;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@JsonTest
public class TestDto {

    @Autowired
    private JacksonTester<BookingResponseDto> bookingResponseJson;
    @Autowired
    private JacksonTester<BookingRequestDto> bookingRequestJson;
    @Autowired
    private JacksonTester<ItemResponseDto> itemResponseJson;
    @Autowired
    private JacksonTester<CommentRequestDto> commentRequestJson;
    @Autowired
    private JacksonTester<ItemRequestResponseDto> itemRequestResponseJson;
    @Autowired
    private JacksonTester<ItemRequestCreateDto> itemRequestCreateJson;
    @Autowired
    private JacksonTester<UserResponseDto> userResponseJson;
    @Autowired
    private JacksonTester<UserCreateDto> userCreateJson;

    @Test
    public void testBookingSerialization() throws IOException {
        BookingResponseDto.BookerDto booker = BookingResponseDto.BookerDto.builder()
                .id(10L)
                .name("Name")
                .build();

        BookingResponseDto.ItemDto item = BookingResponseDto.ItemDto.builder()
                .id(5L)
                .name("Book")
                .build();

        LocalDateTime start = LocalDateTime.of(2025, 07, 20, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 07, 22, 14, 0, 0);

        BookingResponseDto responseDto = BookingResponseDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();

        JsonContent<BookingResponseDto> result = bookingResponseJson.write(responseDto);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.id", 1L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-07-20T14:00:00");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-07-22T14:00:00");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        Assertions.assertThat(result).hasJsonPathNumberValue("$.booker.id", 10L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Name");

        Assertions.assertThat(result).hasJsonPathNumberValue("$.item.id", 5L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Book");
    }

    @Test
    void testBookingDeserialization() throws IOException {
        String jsonContent = """
                {
                    "itemId": 10,
                    "start": "2025-07-20T10:00:00",
                    "end": "2025-07-22T12:00:00"
                }
                """;

        BookingRequestDto resultDto = bookingRequestJson.parse(jsonContent).getObject();

        Assertions.assertThat(resultDto.getItemId()).isEqualTo(10L);
        Assertions.assertThat(resultDto.getStart()).isEqualTo(LocalDateTime.of(2025, 07, 20, 10, 0, 0));
        Assertions.assertThat(resultDto.getEnd()).isEqualTo(LocalDateTime.of(2025, 07, 22, 12, 0, 0));
    }

    @Test
    void testItemSerialization() throws IOException {
        LocalDateTime commentDate = LocalDateTime.of(2025, 8, 1, 15, 45);
        CommentResponseDto comment = CommentResponseDto.builder()
                .id(101L)
                .text("Good")
                .authorName("Name")
                .created(commentDate)
                .build();

        NearestBookingDto lastBooking = new NearestBookingDto(20L, 201L);
        NearestBookingDto nextBooking = new NearestBookingDto(21L, 202L);

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Book")
                .description("Nice")
                .available(true)
                .requestId(55L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();

        JsonContent<ItemResponseDto> result = itemResponseJson.write(itemResponseDto);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.id", 1L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Book");
        Assertions.assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(55L);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.lastBooking.id", 20L);
        Assertions.assertThat(result).hasJsonPathNumberValue("$.lastBooking.bookerId", 201L);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.nextBooking.id", 21L);

        Assertions.assertThat(result).hasJsonPath("$.comments");
        Assertions.assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.comments[0].id", 101L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Name");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2025-08-01T15:45:00");
    }

    @Test
    void testCommentDeserialization() throws IOException {
        String jsonContent = """
                {
                    "text": "Ok"
                }
                """;

        CommentRequestDto resultDto = commentRequestJson.parse(jsonContent).getObject();

        Assertions.assertThat(resultDto.getText()).isEqualTo("Ok");
    }

    @Test
    void testItemRequestResponseSerialization() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(200L)
                .name("book")
                .description("good book")
                .available(true)
                .requestId(1L)
                .build();

        LocalDateTime creationTime = LocalDateTime.of(2025, 9, 15, 11, 30, 0);
        ItemRequestResponseDto responseDto = ItemRequestResponseDto.builder()
                .id(1L)
                .description("need book")
                .requesterId(50L)
                .createdAt(creationTime)
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestResponseDto> result = itemRequestResponseJson.write(responseDto);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.id", 1L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("need book");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(50L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.createdAt").isEqualTo("2025-09-15T11:30:00");

        Assertions.assertThat(result).hasJsonPath("$.items");
        Assertions.assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.items[0].id", 200L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("book");
        Assertions.assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1L);
    }

    @Test
    void testItemRequestCreateDeserialization() throws IOException {
        String jsonContent = """
                {
                    "description": "need book"
                }
                """;

        ItemRequestCreateDto resultDto = itemRequestCreateJson.parse(jsonContent).getObject();

        Assertions.assertThat(resultDto.getDescription()).isEqualTo("need book");
    }

    @Test
    void testUserResponseSerialization() throws IOException {
        UserResponseDto dto = UserResponseDto.builder()
                .id(1L)
                .name("name")
                .email("name@example.com")
                .build();

        JsonContent<UserResponseDto> result = userResponseJson.write(dto);

        Assertions.assertThat(result).hasJsonPathNumberValue("$.id", 1L);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("name@example.com");
    }

    @Test
    void testUserCreateDeserialization() throws IOException {
        String jsonContent = """
                {
                    "name": "name",
                    "email": "name@example.com"
                }
                """;

        UserCreateDto resultDto = userCreateJson.parse(jsonContent).getObject();

        Assertions.assertThat(resultDto.getName()).isEqualTo("name");
        Assertions.assertThat(resultDto.getEmail()).isEqualTo("name@example.com");
    }

}