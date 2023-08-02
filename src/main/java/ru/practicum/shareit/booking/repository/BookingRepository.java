package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByItemId(long itemId);

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

//    @Query(value = "select * " +
//            "from bookings " +
//            "         join public.items i on i.id = bookings.item_id " +
//            "         join public.users u on u.id = i.owner_id " +
//            "where bookings.item_id = :itemId " +
//            "  and i.owner_id = :userId " +
//            "  and bookings.status = 'APPROVED' " +
//            "  and date(bookings.start_date) < :now " +
//            "order by start_date limit 1 ", nativeQuery = true)
//    Booking findAnyBookingLast(@Param("itemId") long itemId, @Param("userId") long userId, @Param("now") LocalDateTime now);
//
//
//    @Query(value = "select * " +
//            "from bookings " +
//            "         join public.items i on i.id = bookings.item_id " +
//            "         join public.users u on u.id = i.owner_id " +
//            "where bookings.item_id = :itemId  " +
//            "  and i.owner_id = :userId " +
//            "  and bookings.status = 'APPROVED' " +
//            "  and date(bookings.start_date) > :now " +
//            "order by start_date desc limit 1 ", nativeQuery = true)
//    Booking findAnyBookingNext(@Param("itemId") long itemId, @Param("userId") long userId, @Param("now") LocalDateTime now);


    Collection<Booking> findAllByItem_Id(long id);
}
