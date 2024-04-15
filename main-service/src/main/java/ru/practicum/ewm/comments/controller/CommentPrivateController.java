package ru.practicum.ewm.comments.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.RequestCommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/comment")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long userId, @PathVariable long eventId,
                                 @RequestBody @Valid RequestCommentDto requestCommentDto) {
        log.info("Данные для добавления комментария {}", requestCommentDto);
        return commentService.addComment(userId, eventId, requestCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable long userId, @PathVariable long commentId,
                                    @RequestBody @Valid RequestCommentDto requestCommentDto) {
        log.info("Данные для обновления комментария {}", requestCommentDto);
        return commentService.updateComment(userId, commentId, requestCommentDto);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Запрос на удаление комментария к событию под номером {}", eventId);
        commentService.deleteComment(userId, eventId);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentsByEven(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Запрос на вывод всех комментариев от пользователя к событию под номером {}", eventId);
        return commentService.getCommentsByEvent(userId, eventId);
    }
}
