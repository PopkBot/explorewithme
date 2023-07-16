package main.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.category.dto.CategoryDto;
import main.category.dto.CategoryInputDto;
import main.category.service.CategoryService;
import main.category.validator.CategoryCreate;
import main.category.validator.CategoryUpdate;
import main.compilation.dto.CompilationDto;
import main.compilation.dto.CompilationInputDto;
import main.compilation.dto.CompilationUpdateDto;
import main.compilation.service.CompilationService;
import main.compilation.validator.CompilationCreate;
import main.compilation.validator.CompilationUpdate;
import main.event.dto.EventDto;
import main.event.dto.EventUpdateDto;
import main.event.dto.GetEventsParamsDto;
import main.event.service.EventService;
import main.event.validator.EventUpdate;
import main.location.dto.LocationDto;
import main.location.dto.LocationGetParamsDto;
import main.location.dto.LocationInputDto;
import main.location.service.LocationService;
import main.location.validator.LocationCreate;
import main.location.validator.LocationUpdate;
import main.user.dto.GetUserListParamsDto;
import main.user.dto.UserDto;
import main.user.dto.UserInputDto;
import main.user.service.UserService;
import main.user.validator.UserCreate;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminUser {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final LocationService locationService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getListOfUsers(@RequestParam(required = false) List<Long> ids,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        GetUserListParamsDto paramsDto = GetUserListParamsDto.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
        log.info("List of users is requested {}", paramsDto);
        return userService.getUsers(paramsDto);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@UserCreate @RequestBody UserInputDto userInputDto) {
        log.info("Request for user creating {}", userInputDto);
        return userService.createUser(userInputDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Request for user deleting {}", userId);
        userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@CategoryCreate @RequestBody CategoryInputDto categoryInputDto) {
        log.info("Request for category creating");
        return categoryService.createCategory(categoryInputDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Request for category deleting");
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@CategoryUpdate @RequestBody CategoryInputDto categoryInputDto,
                                      @PathVariable Long catId) {
        log.info("Request for category patching {}", categoryInputDto);
        return categoryService.patchCategory(categoryInputDto, catId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) List<String> states,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(required = false) Double lat,
                                    @RequestParam(required = false) Double lon,
                                    @RequestParam(required = false) Integer radius,
                                    @RequestParam(required = false) String country,
                                    @RequestParam(required = false) String city,
                                    @RequestParam(required = false) String place) {
        GetEventsParamsDto paramsDto = GetEventsParamsDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .locationGetParamsDto(
                        LocationGetParamsDto.builder()
                                .lon(lon)
                                .lat(lat)
                                .country(country)
                                .city(city)
                                .place(place)
                                .radius(radius)
                                .access(Access.ADMIN)
                                .build())
                .build();
        log.info("Request for events {}", paramsDto.toString());
        return eventService.getEvents(paramsDto);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable Long eventId,
                                @EventUpdate @RequestBody EventUpdateDto eventUpdateDto) {
        eventUpdateDto.setAccess(Access.ADMIN);
        log.info("Request from admin for event {} update {}", eventId, eventUpdateDto);
        return eventService.updateEvent(eventId, eventUpdateDto);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@CompilationCreate @RequestBody CompilationInputDto dto) {
        log.info("Request for compilation creating {}", dto);
        return compilationService.createCompilation(dto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Request for compilation deleting {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilation(@CompilationUpdate @RequestBody CompilationUpdateDto dto,
                                           @PathVariable Long compId) {
        log.info("Request for compilation patching {}", dto);
        return compilationService.patchCompilation(compId, dto);
    }

    @PostMapping("/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto addLocation(@LocationCreate @RequestBody LocationInputDto locationInputDto) {
        log.info("Request from admin for location adding {}", locationInputDto);
        return locationService.addLocationFromController(locationInputDto);

    }

    @PatchMapping("/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto updateLocation(@LocationUpdate @RequestBody LocationInputDto locationInputDto,
                                      @PathVariable Long locationId) {
        locationInputDto.setId(locationId);
        log.info("Request from admin for location update {}", locationInputDto);
        return locationService.updateLocationFromController(locationInputDto);
    }

    @DeleteMapping("/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto deleteLocationByID(@PathVariable Long locationId) {
        log.info("Request for location deleting {}", locationId);
        return locationService.deleteLocationById(locationId);
    }

    @DeleteMapping("/locations")
    @ResponseStatus(HttpStatus.OK)
    public List<LocationDto> deleteUnusedLocations() {
        log.info("Request for unused locations deleting");
        return locationService.deleteUnusedLocations();
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
                                          @RequestParam(defaultValue = "10") Integer size) {
        LocationGetParamsDto dto = LocationGetParamsDto.builder()
                .lon(lon)
                .lat(lat)
                .country(country)
                .city(city)
                .place(place)
                .radius(radius)
                .from(from)
                .size(size)
                .access(Access.ADMIN)
                .build();
        log.info("Request for locations");
        return locationService.getLocations(dto);
    }

    @GetMapping("/locations/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto getLocationById(@PathVariable Long locationId) {
        log.info("Request for location {}", locationId);
        return locationService.getLocationById(locationId);
    }


}
