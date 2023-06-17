package service;

import dto.HitInputDto;
import dto.StatOutDto;
import dto.StatsParamDto;

import java.util.List;

public interface StatService {

    void addHit(HitInputDto hitInputDto);
    List<StatOutDto> getStats(StatsParamDto statsParamDto);

}
