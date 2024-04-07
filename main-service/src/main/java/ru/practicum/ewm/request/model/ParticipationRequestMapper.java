package ru.practicum.ewm.request.model;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtos(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(r -> ParticipationRequestDto.builder()
                        .created(r.getCreated())
                        .event(r.getEvent().getId())
                        .id(r.getId())
                        .requester(r.getRequester().getId())
                        .status(r.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
