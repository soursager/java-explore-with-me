package ru.practicum.ewm.compilation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events;
    private boolean pinned;

    @Length(min = 1, max = 50)
    private String title;
}
