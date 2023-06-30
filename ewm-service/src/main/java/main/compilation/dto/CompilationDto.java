package main.compilation.dto;

import lombok.Data;
import main.event.dto.EventPublicDto;

import java.util.Set;

@Data
public class CompilationDto {
    private Set<EventPublicDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}
