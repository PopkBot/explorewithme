package service;

import dto.HitInputDto;
import dto.StatOutDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.HitMapper;
import model.Hit;
import org.springframework.stereotype.Service;
import repository.HitRepository;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;


    @Override
    public void addHit(HitInputDto hitInputDto) {
        Hit hit = hitRepository.save(hitMapper.convertToHit(hitInputDto));
        log.info("hit {} is added", hit);
    }

    @Override
    public List<StatOutDto> getStats(StatsParamDto statsParamDto) {
        Timestamp start = Timestamp.valueOf(statsParamDto.getStart());
        Timestamp end = Timestamp.valueOf(statsParamDto.getEnd());
        StringBuilder urisBuilder = new StringBuilder();
        for (String uri : statsParamDto.getUris()) {
            urisBuilder.append(uri).append(",");
        }
        urisBuilder.deleteCharAt(urisBuilder.length() - 1);
        List<StatOutDto> statOutDtos;
        if (statsParamDto.isUnique()) {
            statOutDtos = hitRepository.countHitsUnique(start, end, urisBuilder.toString());
            log.info("Statistic returned {}",statOutDtos);
            return statOutDtos;
        }
        statOutDtos = hitRepository.countHits(start, end, urisBuilder.toString());
        log.info("Statistic returned {}",statOutDtos);
        return statOutDtos;
    }
}
