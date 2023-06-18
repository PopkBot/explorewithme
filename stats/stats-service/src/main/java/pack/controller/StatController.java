package pack.controller;

import dto.HitInputDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pack.service.StatService;
import pack.validators.HitCreate;
import projection.StatProjection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static constants.FormatConstants.DATE_TIME_FORMATTER;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@HitCreate @RequestBody HitInputDto hitInputDto) {
        log.info("Hit adding is requested {}", hitInputDto);
        statService.addHit(hitInputDto);
    }

    @GetMapping("/stats")
    public List<StatProjection> getStats(@RequestParam(required = true) String start,
                                         @RequestParam(required = true) String end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        StatsParamDto statsParamDto = StatsParamDto.builder()
                .start(LocalDateTime.parse(start, DATE_TIME_FORMATTER))
                .end(LocalDateTime.parse(end, DATE_TIME_FORMATTER))
                .uris((ArrayList<String>) uris)
                .unique(unique)
                .build();
        log.info("Stats are requested");
        return statService.getStats(statsParamDto);
    }

}
