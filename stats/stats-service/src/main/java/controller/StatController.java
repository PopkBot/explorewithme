package controller;

import dto.HitInputDto;
import dto.StatOutDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import service.StatService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static Constants.FormatConstants.DATE_TIME_FORMATTER;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public void addHit(@RequestBody HitInputDto hitInputDto) {
        log.info("Hit adding is requested {}", hitInputDto);
        statService.addHit(hitInputDto);
    }

    @GetMapping("/stats")
    public List<StatOutDto> getStats(@RequestParam(required = true) String start,
                                     @RequestParam(required = true) String end,
                                     @RequestParam List<String> uris,
                                     @RequestParam(defaultValue = "false") Boolean unique) {
        StatsParamDto statsParamDto = StatsParamDto.builder()
                .start(LocalDateTime.parse(start, DATE_TIME_FORMATTER))
                .end(LocalDateTime.parse(start, DATE_TIME_FORMATTER))
                .uris((ArrayList<String>) uris)
                .unique(unique)
                .build();
        log.info("Stats are requested");
        return statService.getStats(statsParamDto);
    }

}
