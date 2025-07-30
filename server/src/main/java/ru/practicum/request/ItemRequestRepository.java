package ru.practicum.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.id IN :ids")
    List<ItemRequest> findRequestsWithItemsByIdIn(@Param("ids") List<Long> ids);

    @EntityGraph(attributePaths = {"items"})
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId);

    Page<ItemRequest> findByRequesterIdOrderByCreatedDesc(Long requesterId, Pageable pageable);

    Page<ItemRequest> findByRequesterIdNot(Long requesterId, Pageable pageable);

    @Query("SELECT DISTINCT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.id IN :requestIds")
    List<ItemRequest> findWithItemsByIdIn(List<Long> requestIds);
}