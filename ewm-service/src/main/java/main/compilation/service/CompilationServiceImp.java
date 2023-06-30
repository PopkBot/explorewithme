package main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.CustomPageRequest;
import main.compilation.dto.CompilationDto;
import main.compilation.dto.CompilationGetParameters;
import main.compilation.dto.CompilationInputDto;
import main.compilation.dto.CompilationUpdateDto;
import main.compilation.mapper.CompilationMapper;
import main.compilation.model.Compilation;
import main.compilation.repository.CompilationRepository;
import main.event.model.Event;
import main.event.repository.EventRepository;
import main.exceptions.ObjectAlreadyExistsException;
import main.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImp implements CompilationService{

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(CompilationInputDto dto) {
        if(compilationRepository.findByTitle(dto.getTitle()).isPresent()){
            throw new ObjectAlreadyExistsException("Compilation already exists");
        }
        Set<Event> events = eventRepository.findAllByIds(List.copyOf(dto.getEvents()));
        if(events.size()!=dto.getEvents().size()){
            throw new ObjectNotFoundException("Event(s) not found");
        }
        Compilation compilation = compilationRepository.save(makeCompilation(dto,events));
        log.info("Compilation has been created {}",compilation);
        return compilationMapper.convertToDto(compilation);
    }

    private Compilation makeCompilation(CompilationInputDto dto, Set<Event> events){
        Compilation compilation = compilationMapper.convertToCompilation(dto);
        compilation.setEvents(Set.copyOf(events));
        return compilation;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("Compilation not found")
        );
        compilationRepository.delete(compilation);
        log.info("Compilation has been deleted {}",compilation);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(Long id,CompilationUpdateDto dto) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("Compilation not found")
        );
        Set<Event> events = eventRepository.findAllByIds(List.copyOf(dto.getEvents()));
        if(events.size()!=dto.getEvents().size()){
            throw new ObjectNotFoundException("Event(s) not found");
        }
        updateCompilation(compilation,dto,events);
        compilation = compilationRepository.save(compilation);
        log.info("Compilation has been updated {}",compilation);
        return compilationMapper.convertToDto(compilation);
    }

    void updateCompilation(Compilation compilation, CompilationUpdateDto dto ,Set<Event> events){
        if(dto.getPinned()!=null){
            compilation.setPinned(dto.getPinned());
        }
        if(dto.getTitle()!=null){
            compilation.setTitle(dto.getTitle());
        }
        if(dto.getEvents()!=null){
            compilation.setEvents(events);
        }
    }

    @Override
    public List<CompilationDto> getCompilations(CompilationGetParameters parameters) {
        Pageable page = new CustomPageRequest(parameters.getFrom(),parameters.getSize());
        Page<Compilation> compilationPage = compilationRepository.findAllByPinned(parameters.getPinned(),page);
        List<CompilationDto> compilationDtos = compilationPage.getContent().stream()
                .map(compilationMapper::convertToDto).collect(Collectors.toList());
        log.info("List of compilations has been returned {}",compilationDtos);
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("Compilation not found")
        );
        log.info("Compilation has been returned {}",compilation);
        return compilationMapper.convertToDto(compilation);
    }
}
