package main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import main.event.model.Location;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EventInputDto {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    private String title;

}
