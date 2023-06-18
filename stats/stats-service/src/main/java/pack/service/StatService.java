package pack.service;

import dto.HitInputDto;
import dto.StatsParamDto;
import projection.StatProjection;

import java.util.List;

public interface StatService {

    void addHit(HitInputDto hitInputDto);

    List<StatProjection> getStats(StatsParamDto statsParamDto);

}
