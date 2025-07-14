package ru.practicum.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.item.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemOwnerId(Long ownerId);

    List<Booking> findAllByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status NOT IN :statuses " +
            "AND b.end > :start " +
            "AND b.start < :end")
    List<Booking> findConflictingBookings(Long itemId, List<BookingStatus> statuses, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndIsBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime time
    );

    Page<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long authorId, Long itemId, BookingStatus bookingStatus, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime now,
            BookingStatus status
    );

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds AND b.start < :now AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findLastBookingsForItems(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds AND b.start >= :now AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookingsForItems(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByItemInAndStatus(List<Item> ownerItems, BookingStatus bookingStatus);
}