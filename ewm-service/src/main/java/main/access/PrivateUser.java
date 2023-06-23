package main.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.event.dto.EventInputDto;
import main.event.model.Event;
import main.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateUser {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@RequestBody EventInputDto eventInputDto,
                             @PathVariable Long userId){
        log.info("Request for event adding by user {} {}",userId,eventInputDto);
        return eventService.createEvent(eventInputDto,userId);
    }

}
