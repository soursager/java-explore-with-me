package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exeption.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.ParticipationRequestMapper;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.util.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository repository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        List<ParticipationRequest> requests = repository.findAllByRequesterId(userId);
        log.info("Возврат запросов пользователя по id - {} - {}", userId, requests);

        return requests.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Event event = eventService.returnIfExists(eventId);
        User requester = userService.returnIfExists(userId);

        if (event.getInitiator().equals(requester)) {
            throw new ConflictParameterException("Нельзя добавить запрос на собственное событие");
        }

        checkEventParam(event);

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(event.getParticipantLimit() == 0 || !event.isRequestModeration() ?
                        State.CONFIRMED : State.PENDING)
                .build();

        // Увеличиваем счетчк в событии, если статсус запроса подтвержден
        if (participationRequest.getStatus() == State.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventService.updateEvent(event);
        }

        log.info("Созранине запроса на участие в событии - {}", participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(repository.save(participationRequest));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userService.checkExistingUser(userId);
        ParticipationRequest request = returnIfExists(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictParameterException("Нельзя отменять не свой запрос на участие");
        }

        // Если отменяется уже подтвержденный запрос, то обновляем счетчик в событии
        if (request.getStatus() == State.CONFIRMED) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventService.updateEvent(event);
        }

        request.setStatus(State.CANCELED);

        log.info("Сохранение отмененного запроса на участие - {}", request);
        return ParticipationRequestMapper.toParticipationRequestDto(repository.save(request));

    }

    private void checkEventParam(Event event) {
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictParameterException("Нельзя добавить запрос на неопубликованное событие");
        } else if (event.getParticipantLimit() != 0 &&
                repository.countAllByEventIdAndStatus(event.getId(), State.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ConflictParameterException("Превышен лимит на участие в событии");
        }
    }

    private ParticipationRequest returnIfExists(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос по id - " + requestId + " не найден"));
    }
}