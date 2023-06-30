package main.event.service;

import main.event.dto.*;
import main.event.model.Event;

import java.util.List;

public interface EventService {

    EventDto createEvent(EventInputDto eventInputDto, Long userId);

    List<EventDto> getEvents(GetEventsParamsDto paramsDto);

    EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto);

    EventDto getEventById(Long userId, Long eventId);

    EventDto getEventByIdPublic(Long eventId);

    List<EventPublicDto> getEventsPublic(GetEventsParamsDto paramsDto);
}
