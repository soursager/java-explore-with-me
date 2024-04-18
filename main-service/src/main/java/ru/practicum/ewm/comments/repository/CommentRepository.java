package ru.practicum.ewm.comments.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.util.CommentState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findCommentByAuthorAndEvent(User user, Event event);

    List<Comment> findAllByEventAndState(Event event, CommentState commentState);

    List<Comment> findAllByCreatedIsAfterAndCreatedIsBeforeOrderByCreated(LocalDateTime start,
                                                                          LocalDateTime end, Pageable pageable);

    boolean existsByAuthor(User user);
}
