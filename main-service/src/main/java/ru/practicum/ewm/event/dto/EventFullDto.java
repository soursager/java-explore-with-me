package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.util.State;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
public class EventFullDto extends EventShortDto {
    private LocalDateTime createdOn;
    private String description;
    private Location location;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private State state;

    @Builder(builderMethodName = "eventFullDtoBuilder")
    public EventFullDto(String annotation,
                        CategoryDto category,
                        int confirmedRequests,
                        String eventDate,
                        Long id,
                        UserShortDto initiator,
                        boolean paid,
                        String title,
                        Long views,
                        LocalDateTime createdOn,
                        String description,
                        Location location,
                        int participantLimit,
                        LocalDateTime publishedOn,
                        boolean requestModeration,
                        State state) {
        super(annotation, category, confirmedRequests, eventDate, id, initiator, paid, title, views);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
    }
}
