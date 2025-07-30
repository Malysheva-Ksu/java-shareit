package ru.practicum;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.Comment;
import ru.practicum.item.Item;
import ru.practicum.item.dto.ItemResponseDto;
import ru.practicum.item.service.ItemService;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;
    private Booking lastBookingForItem1;
    private Booking nextBookingForItem1;
    private Comment commentForItem1;

    @BeforeEach
    void setUp() {
        owner = User.builder().name("Owner").email("owner@mail.com").build();
        booker = User.builder().name("Booker").email("booker@mail.com").build();
        entityManager.persist(owner);
        entityManager.persist(booker);

        item1 = Item.builder().name("Book").description("Nice book").available(true).owner(owner).build();
        item2 = Item.builder().name("Book2").description("Nice book 2").available(true).owner(owner).build();
        entityManager.persist(item1);
        entityManager.persist(item2);

        lastBookingForItem1 = Booking.builder()
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(9))
                .item(item1).booker(booker).status(BookingStatus.APPROVED).build();

        nextBookingForItem1 = Booking.builder()
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .item(item1).booker(booker).status(BookingStatus.APPROVED).build();

        Booking rejectedBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1).booker(booker).status(BookingStatus.REJECTED).build();

        entityManager.persist(lastBookingForItem1);
        entityManager.persist(nextBookingForItem1);
        entityManager.persist(rejectedBooking);

        commentForItem1 = Comment.builder().text("Great").item(item1).author(booker).created(LocalDateTime.now().minusDays(5)).build();
        entityManager.persist(commentForItem1);

        entityManager.flush();
    }

    @Test
    void getItemsByOwner_whenDataExists_shouldReturnAggregatedDtoList() {
        List<ItemResponseDto> result = itemService.getItemsByOwner(owner.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size(), "Должно быть найдено 2 вещи");

        Optional<ItemResponseDto> item1DtoOpt = result.stream().filter(dto -> dto.getId().equals(item1.getId())).findFirst();
        assertTrue(item1DtoOpt.isPresent(), "DTO для item1 должен присутствовать в результате");
        ItemResponseDto item1Dto = item1DtoOpt.get();

        assertEquals("Book", item1Dto.getName());
        assertNotNull(item1Dto.getLastBooking(), "У item1 должно быть прошлое бронирование");
        assertEquals(lastBookingForItem1.getId(), item1Dto.getLastBooking().getId());

        assertNotNull(item1Dto.getNextBooking(), "У item1 должно быть следующее бронирование");
        assertEquals(nextBookingForItem1.getId(), item1Dto.getNextBooking().getId());

        assertNotNull(item1Dto.getComments());
        assertEquals(1, item1Dto.getComments().size(), "У item1 должен быть один комментарий");
        assertEquals(commentForItem1.getId(), item1Dto.getComments().get(0).getId());

        Optional<ItemResponseDto> item2DtoOpt = result.stream().filter(dto -> dto.getId().equals(item2.getId())).findFirst();
        assertTrue(item2DtoOpt.isPresent(), "DTO для item2 должен присутствовать в результате");
        ItemResponseDto item2Dto = item2DtoOpt.get();

        assertEquals("Book2", item2Dto.getName());
        assertNull(item2Dto.getLastBooking(), "У item2 не должно быть прошлого бронирования");
        assertNull(item2Dto.getNextBooking(), "У item2 не должно быть следующего бронирования");

        assertNotNull(item2Dto.getComments());
        assertTrue(item2Dto.getComments().isEmpty(), "У item2 не должно быть комментариев");
    }
}