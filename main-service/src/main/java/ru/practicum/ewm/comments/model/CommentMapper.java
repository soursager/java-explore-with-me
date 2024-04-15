package ru.practicum.ewm.comments.model;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.RequestCommentDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.util.CommentState;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(User user, Event event, RequestCommentDto requestCommentDto) {
        return Comment.builder()
                .comment(requestCommentDto.getComment())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .state(CommentState.PENDING)
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .author(comment.getAuthor().getName())
                .event(comment.getEvent().getAnnotation())
                .created(comment.getCreated())
                .edited(comment.getEdited())
                .state(comment.getState())
                .build();
    }
}
