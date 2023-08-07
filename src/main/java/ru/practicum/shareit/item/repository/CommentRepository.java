package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByItemId(long itemId);

    @Query("select c from Comment c " +
            "where c.item.id in ?1")
    List<Comment> findAllByItemIds(List<Long> idsList);
}
