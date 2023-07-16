package main.event.mapper;

import constants.FormatConstants;
import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.dto.EventPublicDto;
import main.event.model.Event;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class EventMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public EventMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        configuration.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        configuration.setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        configuration.setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Event convertToEvent(EventInputDto eventInputDto) {
        Event event = modelMapper.map(eventInputDto, Event.class);
        event.setEventDate(LocalDateTime.parse(eventInputDto.getEventDate(),
                FormatConstants.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault()));
        return event;
    }

    public EventDto convertToDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    public EventPublicDto convertToPublicDto(Event event) {
        return modelMapper.map(event, EventPublicDto.class);
    }
}
