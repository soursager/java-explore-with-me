package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest updateRequest, Long compId);

    void deleteCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean isPinned, int from, int size);

    CompilationDto getCompilation(Long compId);
}
