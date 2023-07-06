package main.location.mapper;

import main.location.dto.LocationDto;
import main.location.dto.LocationInputDto;
import main.location.model.Location;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;


@Component
public class LocationMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public LocationMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        configuration.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        configuration.setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        configuration.setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Location convertToModel(LocationInputDto locationInputDto) {
        return modelMapper.map(locationInputDto, Location.class);
    }

    public LocationDto convertToDto(Location location) {
        return modelMapper.map(location, LocationDto.class);
    }

    public LocationDto convertResponseEntityToDto(ResponseEntity<Object> entity){
        LinkedHashMap<String,Object> body = (LinkedHashMap<String,Object>) entity.getBody();
        Object bodyAddress = body.get("address");
        LinkedHashMap<String,String> locBody = (LinkedHashMap<String,String>)bodyAddress;
        return LocationDto.builder()
                .city(locBody.get("city"))
                .country(locBody.get("country"))
                .build();
    }
}
