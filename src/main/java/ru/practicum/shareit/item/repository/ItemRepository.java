package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments c " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY i.id ASC")
    List<Item> findAllByOwnerIdWithDetails(Long ownerId);

    @Query("select i from Item i " +
            "where i.available = true and (lower(i.name) like lower(concat('%', :searchText, '%')) " +
            "or lower(i.description) like lower(concat('%', :searchText, '%')))")
    Page<Item> searchAvailableByText(String searchText, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);
}