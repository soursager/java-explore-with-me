package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.RequestCommentDto;
import ru.practicum.ewm.util.CommentState;

import java.util.List;

public interface CommentService {
    CommentDto addComment(long userId, long eventId, RequestCommentDto requestCommentDto);

    CommentDto updateComment(long userId, long commentId, RequestCommentDto requestCommentDto);

    void deleteComment(long userId, long eventId);

    List<CommentDto> getCommentsByEvent(long userId, long eventId);

    CommentDto updateStateComment(long commentId, CommentState commentState);

    List<CommentDto> getCommentsList(String rangeStart, String rangeEnd, int from, int size);
}
