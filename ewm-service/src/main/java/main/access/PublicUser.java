package main.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.category.dto.CategoryDto;
import main.category.service.CategoryService;
import main.compilation.dto.CompilationDto;
import main.compilation.dto.CompilationGetParameters;
import main.compilation.service.CompilationService;
import main.event.SortType;
import main.event.dto.EventDto;
import main.event.dto.EventPublicDto;
import main.event.dto.GetEventsParamsDto;
import main.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicUser {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getListOfCategories(@RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size){
        log.info("Request for categories list");
        return categoryService.getListOfCategories(from,size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long catId){
        log.info("Request for category {}",catId);
        return categoryService.getCategoryById(catId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventPublicDto> getEvents(@RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(required = false) Boolean onlyAvailable,
                                          @RequestParam(defaultValue = "VIEWS") String sort,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size){
        GetEventsParamsDto dto = GetEventsParamsDto.builder()
                .searchText(text)
                .categories(categories)
                .paid(paid)
                .rangeEnd(rangeEnd)
                .rangeStart(rangeStart)
                .onlyAvailable(onlyAvailable)
                .sort(SortType.valueOf(sort))
                .size(size)
                .from(from)
                .build();
        dto.validate();
        log.info("Request for events {}",dto);
        return eventService.getEventsPublic(dto);
    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventById(@PathVariable Long eventId){
        log.info("Public request for event {}",eventId);
        return eventService.getEventByIdPublic(eventId);
    }

    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "true") Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size){

        CompilationGetParameters dto = CompilationGetParameters.builder()
                .pinned(pinned)
                .from(from)
                .size(size)
                .build();
        log.info("List of compilations has been requested {}",dto);
        return compilationService.getCompilations(dto);
    }

    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable Long compId){

        log.info("Compilation has been requested {}",compId);
        return compilationService.getCompilationById(compId);
    }


}
