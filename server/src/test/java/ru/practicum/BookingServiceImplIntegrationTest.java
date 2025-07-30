package ru.practicum;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.service.BookingService;
import ru.practicum.exception.BookingSelfOwnershipException;
import ru.practicum.item.Item;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = User.builder().name("Item Owner").email("owner@example.com").build();
        booker = User.builder().name("Item Booker").email("booker@example.com").build();

        item = Item.builder()
                .name("Book")
                .description("Nice book")
                .available(true)
                .owner(owner)
                .build();

        entityManager.persist(owner);
        entityManager.persist(booker);
        entityManager.persist(item);
        entityManager.flush();
    }

    @Test
    void createBooking_whenDataIsValid_shouldSaveAndReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto requestDto = new BookingRequestDto(item.getId(), start, end);

        BookingResponseDto responseDto = bookingService.createBooking(booker.getId(), requestDto);

        Assertions.assertNotNull(responseDto);
        Assertions.assertNotNull(responseDto.getId());
        Assertions.assertEquals(BookingStatus.WAITING, responseDto.getStatus());
        Assertions.assertEquals(booker.getId(), responseDto.getBooker().getId());
        Assertions.assertEquals(item.getId(), responseDto.getItem().getId());

        Booking savedBooking = entityManager.find(Booking.class, responseDto.getId());
        Assertions.assertNotNull(savedBooking);
        Assertions.assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
    }

    @Test
    void createBooking_whenBookerIsOwner_shouldThrowException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingRequestDto requestDto = new BookingRequestDto(item.getId(), start, end);

        Assertions.assertThrows(BookingSelfOwnershipException.class, () -> {
            bookingService.createBooking(owner.getId(), requestDto);
        });
    }

    @Test
    void approveBooking_whenUserIsOwner_shouldChangeStatusToApproved() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        BookingResponseDto responseDto = bookingService.approveBooking(owner.getId(), booking.getId(), true);

        Assertions.assertEquals(BookingStatus.APPROVED, responseDto.getStatus());

        Booking updatedBooking = entityManager.find(Booking.class, booking.getId());
        Assertions.assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void getBookingById_whenUserIsBooker_shouldReturnBooking() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        BookingResponseDto foundBooking = bookingService.getBookingById(booker.getId(), booking.getId());

        Assertions.assertNotNull(foundBooking);
        Assertions.assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    void getBookingsForUser_whenStateIsAll_shouldReturnUserBookings() {
        Booking booking1 = Booking.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).item(item).booker(booker).status(BookingStatus.WAITING).build();
        Booking booking2 = Booking.builder().start(LocalDateTime.now().plusDays(3)).end(LocalDateTime.now().plusDays(4)).item(item).booker(booker).status(BookingStatus.APPROVED).build();
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        List<BookingResponseDto> userBookings = bookingService.getBookingsForUser(booker.getId(), BookingState.ALL, 0, 10);

        Assertions.assertEquals(2, userBookings.size());
        Assertions.assertTrue(userBookings.stream().anyMatch(b -> b.getId().equals(booking1.getId())));
        Assertions.assertTrue(userBookings.stream().anyMatch(b -> b.getId().equals(booking2.getId())));
    }

    @Test
    void getBookingsForOwner_whenStateIsWaiting_shouldReturnOwnerBookings() {
        Booking waitingBooking = Booking.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).item(item).booker(booker).status(BookingStatus.WAITING).build();
        Booking approvedBooking = Booking.builder().start(LocalDateTime.now().plusDays(3)).end(LocalDateTime.now().plusDays(4)).item(item).booker(booker).status(BookingStatus.APPROVED).build();
        entityManager.persist(waitingBooking);
        entityManager.persist(approvedBooking);
        entityManager.flush();

        List<BookingResponseDto> ownerBookings = bookingService.getBookingsForOwner(owner.getId(), BookingState.WAITING, 0, 10);

        Assertions.assertEquals(1, ownerBookings.size());
        Assertions.assertEquals(waitingBooking.getId(), ownerBookings.get(0).getId());
        Assertions.assertEquals(BookingStatus.WAITING, ownerBookings.get(0).getStatus());
    }
}