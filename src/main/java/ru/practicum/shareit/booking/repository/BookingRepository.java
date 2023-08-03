package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= ?2 " +
            "and b.end >= ?2 " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsBookerCurrent(long bookerId, LocalDateTime now);

    @Query("select b FROM Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end <= ?2 " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsBookerPast(long bookerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start >= ?2 " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsBookerFuture(long bookerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and upper(b.bookingStatus) = upper(?2) " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsByBookingStatusForBooker(long bookerId, String status);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    Collection<Booking> findAllByOwnerId(long ownerId);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= ?2 " +
            "and b.end >= ?2 " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsOwnerCurrent(long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end <= ?2 " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsOwnerPast(long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start >= ?2 " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsOwnerFuture(long bookerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and upper(b.bookingStatus) = upper(?2) " +
            "order by b.start desc ")
    Collection<Booking> findAllBookingsByBookingStatusForOwner(long bookerId, String status);

    Collection<Booking> findAllByItem_Id(long id);

    Booking findBookingByBooker_IdAndItem_Id(long bookerId, long itemId);
}
