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
                                    @RequestParam(defaultValue = "10") Integer size) {
        GetEventsParamsDto paramsDto = GetEventsParamsDto.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        paramsDto.validate();
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

}
