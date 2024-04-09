package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.util.SearchAdminParameters;
import ru.practicum.ewm.util.SearchParameters;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getEvents(SearchParameters params, HttpServletRequest request);

    List<EventFullDto> getEventsByAdmin(SearchAdminParameters param);

    List<EventShortDto> getEventsByUserId(Long userId, int from, int size);

    EventFullDto getEventByIdAndUserId(Long eventId, Long userId);

    EventFullDto updateEventByIdAndUserId(Long eventId, Long userId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventByIdAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Event returnIfExists(Long eventId);

    void updateEvent(Event event);

    List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatusByUserAndEvent(Long userId, Long eventId,
                                                                      EventRequestStatusUpdateRequest updateRequest);
}
