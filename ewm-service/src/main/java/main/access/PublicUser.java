package main.access;

import constants.FormatConstants;
import dto.HitInputDto;
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
import main.location.dto.LocationDto;
import main.location.dto.LocationGetParamsDto;
import main.location.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicUser {

    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final LocationService locationService;

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getListOfCategories(@RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request for categories list");
        return categoryService.getListOfCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Request for category {}", catId);
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
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) Double lat,
                                          @RequestParam(required = false) Double lon,
                                          @RequestParam(required = false) Integer radius,
                                          @RequestParam(required = false) String country,
                                          @RequestParam(required = false) String city,
                                          @RequestParam(required = false) String place,
                                          HttpServletRequest request) {
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
                .locationGetParamsDto(
                        LocationGetParamsDto.builder()
                                .lon(lon)
                                .lat(lat)
                                .country(country)
                                .city(city)
                                .place(place)
                                .radius(radius)
                                .access(Access.PUBLIC)
                                .build()
                )
                .build();
        HitInputDto hitInputDto = HitInputDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("Explore with me")
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).format(FormatConstants.DATE_TIME_FORMATTER))
                .build();
        log.info("Request for events {}", dto);
        return eventService.getEventsPublic(dto, hitInputDto);
    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventById(@PathVariable Long eventId,
                                 HttpServletRequest request) {
        HitInputDto hitInputDto = HitInputDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("Explore with me")
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).format(FormatConstants.DATE_TIME_FORMATTER))
                .build();
        log.info("Public request for event {}", eventId);
        return eventService.getEventByIdPublic(eventId, hitInputDto);
    }

    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {

        CompilationGetParameters dto = CompilationGetParameters.builder()
                .pinned(pinned)
                .from(from)
                .size(size)
                .build();
        log.info("List of compilations has been requested {}", dto);
        return compilationService.getCompilations(dto);
    }

    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable Long compId) {

        log.info("Compilation has been requested {}", compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping("/locations")
    @ResponseStatus(HttpStatus.OK)
    public List<LocationDto> getLocations(@RequestParam(required = false) Double lat,
                                          @RequestParam(required = false) Double lon,
                                          @RequestParam(required = false) Integer radius,
                                          @RequestParam(required = false) String country,
                                          @RequestParam(required = false) String city,
                                          @RequestParam(required = false) String place,
                                          @RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size
    ) {
        LocationGetParamsDto dto = LocationGetParamsDto.builder()
                .lon(lon)
                .lat(lat)
                .country(country)
                .city(city)
                .place(place)
                .radius(radius)
                .from(from)
                .size(size)
                .access(Access.PUBLIC)
                .build();
        log.info("Request for locations");
        return locationService.getLocations(dto);
    }

    @GetMapping("/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto getLocationById(@PathVariable Long locationId) {
        log.info("Request for location {}", locationId);
        return locationService.getLocationByIdPublic(locationId);
    }

}
