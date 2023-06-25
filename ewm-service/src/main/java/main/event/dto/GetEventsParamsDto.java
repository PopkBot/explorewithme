package main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.event.SortType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetEventsParamsDto {

    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
    private Integer from;
    private Integer size;
    private String searchText;
    private Boolean paid;
    private Boolean onlyAvailable;
    private SortType sort;

}
