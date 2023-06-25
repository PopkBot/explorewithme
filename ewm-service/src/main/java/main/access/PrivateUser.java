package main.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.dto.EventUpdateDto;
import main.event.dto.GetEventsParamsDto;
import main.event.service.EventService;
import main.request.dto.RequestDto;
import main.request.dto.StatusSettingDto;
import main.request.dto.StatusSettingInputDto;
import main.request.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateUser {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@RequestBody EventInputDto eventInputDto,
                                @PathVariable Long userId){
        log.info("Request for event adding by user {} {}",userId,eventInputDto);
        return eventService.createEvent(eventInputDto,userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId){
        log.info("Request for request creating, userId {}, eventId {}",userId,eventId);
        return requestService.createRequest(userId,eventId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getUsersRequests(@PathVariable Long userId){
        log.info("Request for user`s {} requests",userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId){
        log.info("Request for canceling user`s {} request {}",userId,requestId);
        return requestService.cancelRequest(userId,requestId);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getUserEvents(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size){
        GetEventsParamsDto paramsDto = GetEventsParamsDto.builder()
                .users(List.of(userId))
                .from(from)
                .size(size)
                .build();
        log.info("Request for user`s {} events {}",userId,paramsDto);

        return eventService.getEvents(paramsDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventById(@PathVariable Long userId, @PathVariable Long eventId){
        log.info("Request for user`s {} event {}",userId, eventId);
        return eventService.getEventById(userId,eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestBody EventUpdateDto eventUpdateDto){
        eventUpdateDto.setAccess(Access.PRIVATE);
        log.info("Request from user {} for event {} update {}", userId, eventId,eventUpdateDto);
        return eventService.updateEvent(eventId,eventUpdateDto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestsForEvent(@PathVariable Long userId, @PathVariable Long eventId){
        log.info("Request for event {} requests",eventId);
        return requestService.getEventRequests(userId,eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public StatusSettingDto setRequestsStatus(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @RequestBody StatusSettingInputDto dto){
        log.info("Request for status setting {}",dto);
        return requestService.setStatusOfRequests(userId,eventId,dto);
    }

}
