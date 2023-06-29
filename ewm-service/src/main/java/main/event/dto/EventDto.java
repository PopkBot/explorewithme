package main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import constants.FormatConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.category.dto.CategoryDto;
import main.event.model.Location;
import main.user.dto.UserDto;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatConstants.DATE_TIME_PATTERN)
    private ZonedDateTime createdOn;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatConstants.DATE_TIME_PATTERN)
    private ZonedDateTime eventDate;
    private UserDto initiator;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatConstants.DATE_TIME_PATTERN)
    private ZonedDateTime publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Integer views;
    private Location location;
}
