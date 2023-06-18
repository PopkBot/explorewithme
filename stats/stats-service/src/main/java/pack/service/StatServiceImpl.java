package pack.service;

import dto.HitInputDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pack.mapper.HitMapper;
import pack.model.Hit;
import pack.repository.HitRepository;
import pack.validators.StatsParamValidation;
import projection.StatProjection;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatServiceImpl implements StatService {

    private final HitRepository hitRepository;
    private final HitMapper hitMapper;


    @Override
    @Transactional
    public void addHit(HitInputDto hitInputDto) {
        Hit hit = hitRepository.save(hitMapper.convertToHit(hitInputDto));
        log.info("hit {} is added", hit);
    }

    @Override
    public List<StatProjection> getStats(StatsParamDto statsParamDto) {
        List<StatProjection> statOutDtos = queryForStats(statsParamDto);
        log.info("Statistic returned {}", statOutDtos);
        return statOutDtos;
    }

    private List<StatProjection> queryForStats(@StatsParamValidation StatsParamDto statsParamDto) {
        Timestamp start = Timestamp.valueOf(statsParamDto.getStart());
        Timestamp end = Timestamp.valueOf(statsParamDto.getEnd());
        if (statsParamDto.getUris() == null && statsParamDto.getUnique()) {
            return hitRepository.countHitsUnique(start, end);
        }
        if (statsParamDto.getUris() == null) {
            return hitRepository.countHits(start, end);
        }
        if (statsParamDto.getUnique()) {
            return hitRepository.countHitsUnique(start, end, statsParamDto.getUris());
        }
        return hitRepository.countHits(start, end, statsParamDto.getUris());
    }

}
