package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentDtoMapper {
    public Comment mapToComment(CommentDto commentDtoRequest, Item item, User author) {
        return new Comment(commentDtoRequest.getId(),
                commentDtoRequest.getText(),
                item,
                author,
                commentDtoRequest.getCreated());
    }

    public CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}