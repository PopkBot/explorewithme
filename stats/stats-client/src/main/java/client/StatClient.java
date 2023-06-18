package client;

import dto.HitInputDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import pack.service.StatService;
import pack.validators.HitCreate;
import projection.StatProjection;

import java.util.List;

@RequiredArgsConstructor
@Validated
public class StatClient {

    private final StatService statService;

    public void addHit(@HitCreate HitInputDto hitInputDto) {
        statService.addHit(hitInputDto);
    }

    public List<StatProjection> getStats(StatsParamDto statsParamDto) {
        return statService.getStats(statsParamDto);
    }
}
