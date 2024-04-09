package ru.practicum.ewm.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class SearchSpecs {
    public static Specification<Event> searchByText(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                        "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + text.toLowerCase() + "%")));
    }

    public static Specification<Event> searchCategories(List<Long> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
    }

    public static Specification<Event> searchByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(userIds));
    }

    public static Specification<Event> searchStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states));
    }

    public static Specification<Event> isPaid(Boolean paid) {
        if (paid == null) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
    }

    public static Specification<Event> rangeStart(LocalDateTime start) {

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                        start == null ? LocalDateTime.now() : start));
    }

    public static Specification<Event> rangeEnd(LocalDateTime end) {
        if (end == null) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("eventDate"), end));
    }

    public static Specification<Event> sortBy(String sort) {
        if (sort == null || sort.isBlank()) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> {
            query.orderBy(sort.equals("EVENT_DATE") ?
                    criteriaBuilder.asc(root.get("eventDate")) : criteriaBuilder.desc(root.get("views")));
            return criteriaBuilder.conjunction();
        });
    }

    public static Specification<Event> onlyAvailable(boolean onlyAvailable) {
        return (root, query, criteriaBuilder) -> {
            if (onlyAvailable) {
                return criteriaBuilder.or(
                        criteriaBuilder.lessThan(
                                root.get("confirmedRequests"),
                                root.get("participantLimit")
                        ),
                        criteriaBuilder.equal(root.get("participantLimit"), 0)
                );
            }
            return null;
        };
    }
}
