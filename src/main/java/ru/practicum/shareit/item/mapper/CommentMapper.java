package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

@Component
public class CommentMapper {

    public CommentDto convertToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem(), comment.getAuthor(), comment.getCreated());
    }

    public Comment convertToComment(CommentDto commentDto) {
        return new Comment(commentDto.getId(), commentDto.getText(), commentDto.getItem(), commentDto.getAuthor(), commentDto.getCreated());
    }
}
