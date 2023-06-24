package main.request.mapper;

import constants.FormatConstants;
import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.model.Event;
import main.request.dto.RequestDto;
import main.request.model.Request;
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
public class RequestMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public RequestMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        configuration.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        configuration.setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        configuration.setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public RequestDto convertToDto(Request request){
        return modelMapper.map(request, RequestDto.class);
    }
}
