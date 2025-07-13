package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private CommentMapper commentMapper;
    private User author;
    private Item item;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();
        author = new User(1L, "John Doe", "john@example.com");
        item = new Item(2L, "Drill", "Powerful drill", true, author, null);
    }

    @Test
    void convertToCommentDto_shouldMapCorrectly() {
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setId(10L);
        comment.setText("Nice item!");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(created);

        CommentDto dto = commentMapper.convertToCommentDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(comment.getId());
        assertThat(dto.getText()).isEqualTo(comment.getText());
        assertThat(dto.getAuthorName()).isEqualTo(author.getName());
        assertThat(dto.getCreated()).isEqualTo(created);
    }

    @Test
    void convertToComment_shouldMapCorrectly() {
        CommentDto dto = new CommentDto(null, "Looks good!", author.getName(), null);

        Comment comment = commentMapper.convertToComment(dto, item, author);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo(dto.getText());
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getItem()).isEqualTo(item);
        assertThat(comment.getCreated()).isNotNull();
    }
}