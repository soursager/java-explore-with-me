package ru.practicum.ewm.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.RequestCommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.model.CommentMapper;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exeption.*;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.CommentState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public CommentDto addComment(long userId, long eventId, RequestCommentDto requestCommentDto) {
        User user = validateUser(userId);
        Event event = validateEvent(eventId);
        if (commentRepository.existsByAuthor(user)) {
            throw new ConflictException("Нельзя оставить комментарий повторно, вы можете обновить " +
                    "свой прошлый комментарий");
        }
        Comment comment;
        try {
            comment = commentRepository.save(CommentMapper.toComment(user, event, requestCommentDto));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Нарушение целостности данных");
        }
        log.info("Добавлен новый комментарий {}", comment);
        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(long userId, long commentId, RequestCommentDto requestCommentDto) {
        User user = validateUser(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден или недоступен"));
        if (!user.equals(comment.getAuthor())) {
            throw new ValidationException("Данный пользователь не может внести изменения в комментарий");
        }
        comment.setComment(requestCommentDto.getComment());
        comment.setEdited(LocalDateTime.now());
        log.info("Изменения сохранены {}", comment);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(long userId, long eventId) {
        User user = validateUser(userId);
        Event event = validateEvent(eventId);
        Comment comment = commentRepository.findCommentByAuthorAndEvent(user, event);
        log.info("Комментарий удален {}", comment);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEvent(long userId, long eventId) {
        Event event = validateEvent(eventId);
        List<Comment> commentList = commentRepository.findAllByEventAndState(event, CommentState.PUBLISHED);
        List<CommentDto> commentDtos = getCommentDtos(commentList);
        log.info("Получен список комментариев {} к Event {} ", commentDtos, event);
        return commentDtos;
    }

    @Override
    @Transactional
    public CommentDto updateStateComment(long commentId, CommentState commentState) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден или недоступен"));
        if (commentState == null) {
            throw new NotFoundException("Запрос составлен некорректно");
        }
        if (commentState == CommentState.PUBLISHED) {
            comment.setState(CommentState.PUBLISHED);
        }
        if (commentState == CommentState.CANCELED) {
            comment.setState(CommentState.CANCELED);
        }
        commentRepository.save(comment);
        log.info("Статус комментария изменен на {}", comment.getState());
        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsList(String rangeStart, String rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime start = LocalDateTime.parse(rangeStart, dateFormatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, dateFormatter);
        if (end.isBefore(start)) {
            throw new ValidationException("Запрос составлен некорректно");
        }
        List<Comment> commentList = commentRepository.findAllByCreatedIsAfterAndCreatedIsBeforeOrderByCreated(start,
                end, pageable);
        log.info("Получен список комментариев с заданными параметрами фильтрации {}", commentList);
        return getCommentDtos(commentList);
    }

    private User validateUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден или недоступен"));
    }

    private Event validateEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдена или недоступна"));
    }

    private List<CommentDto> getCommentDtos(List<Comment> commentList ) {
        return commentList.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }
}
