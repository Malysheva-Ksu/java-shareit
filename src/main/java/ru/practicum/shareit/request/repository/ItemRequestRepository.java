package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.requester.id = :requesterId ORDER BY ir.createdAt DESC")
    Page<ItemRequest> findOwnRequests(Long requesterId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.requester.id <> :requesterId ORDER BY ir.createdAt DESC")
    Page<ItemRequest> findOtherUsersRequests(Long requesterId, Pageable pageable);
}