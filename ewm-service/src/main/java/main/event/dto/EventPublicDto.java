package main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import constants.FormatConstants;
import lombok.Data;
import main.category.dto.CategoryDto;
import main.event.model.Location;
import main.user.dto.UserDto;

import java.time.ZonedDateTime;

@Data
public class EventPublicDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatConstants.DATE_TIME_PATTERN)
    private ZonedDateTime eventDate;
    private UserDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
