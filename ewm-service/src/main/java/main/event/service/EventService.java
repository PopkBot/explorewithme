package main.event.service;

import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.dto.EventUpdateDto;
import main.event.dto.GetEventsParamsDto;
import main.event.model.Event;

import java.util.List;

public interface EventService {

    EventDto createEvent(EventInputDto eventInputDto, Long userId);

    List<EventDto> getEvents(GetEventsParamsDto paramsDto);

    EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto);

    EventDto getEventById(Long userId, Long eventId);

    EventDto getEventByIdPublic(Long eventId);

    List<EventDto> getEventsPublic(GetEventsParamsDto paramsDto);
}
