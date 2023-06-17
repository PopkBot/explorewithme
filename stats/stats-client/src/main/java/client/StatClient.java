package client;

import Constants.FormatConstants;
import dto.HitInputDto;
import dto.StatOutDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import service.StatService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StatClient {

    private final StatService statService;

    public void addHit(HitInputDto hitInputDto){
        statService.addHit(hitInputDto);
    }

    public List<StatOutDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        StatsParamDto statsParamDto = StatsParamDto.builder()
                .start(LocalDateTime.parse(start, FormatConstants.DATE_TIME_FORMATTER))
                .end(LocalDateTime.parse(start, FormatConstants.DATE_TIME_FORMATTER))
                .uris((ArrayList<String>) uris)
                .unique(unique)
                .build();
        return statService.getStats(statsParamDto);
    }
}
