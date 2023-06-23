package main.event.dto;

import lombok.Data;
import main.event.model.Location;

import java.time.LocalDateTime;

@Data
public class EventInputDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;

}
