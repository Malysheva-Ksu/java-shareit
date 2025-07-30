package ru.practicum;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.Item;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.dto.ItemRequestResponseDto;
import ru.practicum.request.service.ItemRequestService;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private EntityManager entityManager;

    private User requester1;
    private User requester2;
    private User viewer;
    private ItemRequest request1;
    private ItemRequest request2;
    private Item item1;

    @BeforeEach
    void setUp() {
        requester1 = User.builder().name("Requester One").email("req1@example.com").build();
        requester2 = User.builder().name("Requester Two").email("req2@example.com").build();
        viewer = User.builder().name("Viewer User").email("viewer@example.com").build();

        entityManager.persist(requester1);
        entityManager.persist(requester2);
        entityManager.persist(viewer);

        request1 = ItemRequest.builder()
                .description("Need a book")
                .requester(requester1)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        request2 = ItemRequest.builder()
                .description("Looking for a book")
                .requester(requester2)
                .created(LocalDateTime.now())
                .build();

        entityManager.persist(request1);
        entityManager.persist(request2);

        item1 = Item.builder()
                .name("book")
                .description("Nice book")
                .available(true)
                .owner(viewer)
                .request(request1)
                .build();

        entityManager.persist(item1);
        entityManager.flush();
    }

    @Test
    void findAll_whenOtherUsersHaveRequests_shouldReturnEnrichedAndSortedRequests() {
        List<ItemRequestResponseDto> result = requestService.findAll(viewer.getId(), 0, 10);

        assertEquals(2, result.size());

        ItemRequestResponseDto firstResult = result.get(0);
        ItemRequestResponseDto secondResult = result.get(1);

        assertEquals(request2.getId(), firstResult.getId());
        assertEquals(request1.getId(), secondResult.getId());

        assertTrue(firstResult.getItems().isEmpty(), "Request should have no items");

        assertFalse(secondResult.getItems().isEmpty(), "Request should have one item");
        assertEquals(1, secondResult.getItems().size());
        assertEquals(item1.getId(), secondResult.getItems().get(0).getId());
        assertEquals("book", secondResult.getItems().get(0).getName());
    }
}