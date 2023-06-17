package dto;

import Constants.FormatConstants;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@Builder
public class StatsParamDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private ArrayList<String> uris;
    private boolean unique;

}
