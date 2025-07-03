package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findTopByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long bookerId, LocalDateTime now, BookingStatus status);

    Booking findTopByItemIdAndStartAfterAndStatusOrderByStartAsc(Long bookerId, LocalDateTime now, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime endBefore);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND :now BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND :now BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);
}
