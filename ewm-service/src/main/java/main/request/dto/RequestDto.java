package main.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import constants.FormatConstants;
import lombok.Data;
import main.event.State;

import java.time.ZonedDateTime;

@Data
public class RequestDto {
    private Long id;
    private Long event;
    private Long requester;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FormatConstants.DATE_TIME_PATTERN)
    private ZonedDateTime created;
    private State status;
}
