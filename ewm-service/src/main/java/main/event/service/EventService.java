package main.event.service;

import main.event.dto.EventInputDto;
import main.event.model.Event;

public interface EventService {

    Event createEvent(EventInputDto eventInputDto, Long userId);
}
