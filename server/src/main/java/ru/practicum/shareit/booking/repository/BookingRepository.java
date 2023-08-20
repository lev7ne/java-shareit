package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 ")
    List<Booking> findAllByBookerId(long bookerId, Pageable page);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start <= ?2 " +
            "and b.end >= ?2 ")
    List<Booking> findAllBookingsBookerCurrent(long bookerId, LocalDateTime now, Pageable page);

    @Query("select b FROM Booking b " +
            "where b.booker.id = ?1 " +
            "and b.end <= ?2")
    List<Booking> findAllBookingsBookerPast(long bookerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start >= ?2 ")
    List<Booking> findAllBookingsBookerFuture(long bookerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and upper(b.bookingStatus) = upper(?2) ")
    List<Booking> findAllBookingsByBookingStatusForBooker(long bookerId, String status, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 ")
    List<Booking> findAllByOwnerId(long ownerId, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start <= ?2 " +
            "and b.end >= ?2 ")
    List<Booking> findAllBookingsOwnerCurrent(long ownerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end <= ?2 ")
    List<Booking> findAllBookingsOwnerPast(long ownerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.start >= ?2 ")
    List<Booking> findAllBookingsOwnerFuture(long bookerId, LocalDateTime now, Pageable page);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and upper(b.bookingStatus) = upper(?2) ")
    List<Booking> findAllBookingsByBookingStatusForOwner(long bookerId, String bookingStatus, Pageable page);

    List<Booking> findAllByItem_IdAndBooker_IdAndBookingStatus(long itemId, long bookerIds, BookingStatus bookingStatus);

    @Query("select b from Booking b " +
            "where b.item.id in ?1 " +
            "and upper(b.bookingStatus) = upper(?2) ")
    List<Booking> findAllByItemIdAndBookingStatus(List<Long> itemIdsList, String bookingStatus);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start < ?2 " +
            "and b.bookingStatus = ?3 ")
    List<Booking> findLastApprovedBookingByItemId(long itemId, LocalDateTime now, BookingStatus bookingStatus, Sort sort);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start >= ?2 " +
            "and b.bookingStatus = ?3 ")
    List<Booking> findNextApprovedBookingByItemId(long itemId, LocalDateTime now, BookingStatus bookingStatus, Sort sort);

}

