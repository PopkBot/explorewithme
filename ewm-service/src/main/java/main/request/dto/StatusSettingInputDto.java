package main.request.dto;

import lombok.Data;
import main.event.State;

import java.util.List;

@Data
public class StatusSettingInputDto {
    private List<Long> requestIds;
    private State status;
}
