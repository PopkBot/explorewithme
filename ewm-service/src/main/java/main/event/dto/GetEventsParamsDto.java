package main.event.dto;

import constants.FormatConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.event.SortType;
import main.exceptions.ValidationException;
import main.location.dto.LocationGetParamsDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private LocationGetParamsDto locationGetParamsDto;

    public void validate() {
        if (this.getRangeEnd() != null && this.getRangeStart() != null) {
            ZonedDateTime start = LocalDateTime.parse(this.getRangeStart(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime end = LocalDateTime.parse(this.getRangeEnd(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            if (start.isAfter(end)) {
                throw new ValidationException("End date cannot be before start");
            }
        }
        if (locationGetParamsDto != null) {
            locationGetParamsDto.validate();
        }
    }

}
