package main.event.dto;

import lombok.Data;
import main.event.State;

@Data
public class EventUpdateDto extends EventInputDto{
    private State stateAction;
}
