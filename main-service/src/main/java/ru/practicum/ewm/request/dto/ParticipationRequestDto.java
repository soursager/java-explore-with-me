package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.util.State;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationRequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private State status;
}
