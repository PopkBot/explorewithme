package main.event.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import main.access.Access;
import main.event.State;

@Data
@SuperBuilder
public class EventUpdateDto extends EventInputDto {
    private State stateAction;
    private Access access;
    private Long userId;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
}
