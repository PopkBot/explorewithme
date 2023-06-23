package main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.category.dto.CategoryDto;
import main.category.model.Category;
import main.event.model.Location;
import main.user.dto.UserDto;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private ZonedDateTime created;
    private String description;
    private ZonedDateTime eventDate;
    private UserDto initiator;
    private Boolean paid;
    private Integer participantLimit;
    private ZonedDateTime published;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Integer views;
    private Location location;
}
