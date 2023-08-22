package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select it from ItemRequest it" +
            " join Item i on it.id = i.itemRequest.id" +
            " where it.requester.id != ?1")
    List<ItemRequest> findAllByRequesterId(long requesterId, Pageable page);

    @Query("select it from ItemRequest it" +
            " left join Item i on it.id = i.itemRequest.id" +
            " where it.requester.id = ?1")
    List<ItemRequest> findAllByRequesterId(long requesterId);


}
