package pack.mapper;

import constants.FormatConstants;
import dto.HitDto;
import dto.HitInputDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pack.model.Hit;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class HitMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public HitMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        configuration.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        configuration.setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        configuration.setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Hit convertToHit(HitInputDto hitInputDto) {
        Hit hit = modelMapper.map(hitInputDto, Hit.class);
        hit.setCreated(LocalDateTime.parse(hitInputDto.getTimestamp(),
                FormatConstants.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault()));
        return hit;
    }

    public HitDto convertToDto(Hit hit) {
        return modelMapper.map(hit, HitDto.class);
    }
}
