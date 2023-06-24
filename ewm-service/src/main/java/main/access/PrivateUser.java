package main.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.service.EventService;
import main.request.dto.RequestDto;
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

    @PostMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId, @PathVariable Long eventId){
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

}
