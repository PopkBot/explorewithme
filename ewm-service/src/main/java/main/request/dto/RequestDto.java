package main.request.dto;

import lombok.Data;
import main.event.State;

import java.time.ZonedDateTime;

@Data
public class RequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private ZonedDateTime created;
    private State state;
}
