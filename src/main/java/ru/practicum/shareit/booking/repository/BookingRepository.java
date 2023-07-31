package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByItemId(long itemId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= ?2 " +
            "and b.end >= ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsBookerCurrent(long bookerId, LocalDateTime now);

    @Query("select b FROM Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end <= ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsBookerPast(long bookerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start >= ?2 " +
            "order by b.start desc ")
    List<Booking> findAllBookingsBookerFuture(long bookerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and upper(b.bookingStatus) = upper(?2) " +
            "order by b.start desc ")
    List<Booking> findAllBookingsByBookingStatus(long bookerId, String status);



}
