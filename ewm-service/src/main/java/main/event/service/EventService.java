package main.event.service;

import dto.HitInputDto;
import main.event.dto.*;

import java.util.List;

public interface EventService {

    EventDto createEvent(EventInputDto eventInputDto, Long userId);

    List<EventDto> getEvents(GetEventsParamsDto paramsDto);

    EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto);

    EventDto getEventById(Long userId, Long eventId);

    EventDto getEventByIdPublic(Long eventId, HitInputDto hitDto);

    List<EventPublicDto> getEventsPublic(GetEventsParamsDto paramsDto, HitInputDto hitDto);
}
