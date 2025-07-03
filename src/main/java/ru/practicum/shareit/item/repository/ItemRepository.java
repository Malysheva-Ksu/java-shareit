package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long ownerId);

    List<Item> findAllByOwner_IdOrderByIdAsc(Long ownerId);

    @Query("select i from Item i " +
            "where i.available = true and (lower(i.name) like lower(concat('%', :searchText, '%')) " +
            "or lower(i.description) like lower(concat('%', :searchText, '%')))")
    List<Item> searchAvailableByText(String searchText);
}