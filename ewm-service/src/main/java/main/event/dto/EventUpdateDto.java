package main.event.dto;

import lombok.Data;
import main.access.Access;
import main.event.State;

@Data
public class EventUpdateDto extends EventInputDto{
    private State stateAction;
    private Access access;
    private Long userId;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
}
