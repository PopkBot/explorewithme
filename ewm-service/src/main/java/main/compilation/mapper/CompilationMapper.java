package main.compilation.mapper;

import main.compilation.dto.CompilationDto;
import main.compilation.dto.CompilationInputDto;
import main.compilation.model.Compilation;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompilationMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public CompilationMapper() {
        this.modelMapper = new ModelMapper();
        Configuration configuration = modelMapper.getConfiguration();
        configuration.setFieldAccessLevel(Configuration.AccessLevel.PUBLIC);
        configuration.setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR);
        configuration.setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        configuration.setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Compilation convertToCompilation(CompilationInputDto categoryInputDto) {
        return modelMapper.map(categoryInputDto, Compilation.class);
    }

    public CompilationDto convertToDto(Compilation compilation) {
        return modelMapper.map(compilation, CompilationDto.class);
    }
}
