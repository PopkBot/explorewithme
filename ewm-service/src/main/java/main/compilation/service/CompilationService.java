package main.compilation.service;

import main.compilation.dto.CompilationDto;
import main.compilation.dto.CompilationGetParameters;
import main.compilation.dto.CompilationInputDto;
import main.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(CompilationInputDto dto);

    void deleteCompilation(Long id);

    CompilationDto patchCompilation(Long id,CompilationUpdateDto dto);

    List<CompilationDto> getCompilations(CompilationGetParameters parameters);

    CompilationDto getCompilationById(Long id);
}
