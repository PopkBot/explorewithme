package pack.service;

import dto.HitInputDto;
import dto.StatsParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pack.mapper.HitMapper;
import pack.model.Hit;
import pack.repository.HitRepository;
import projection.StatProjection;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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
        if (statsParamDto.getUris() == null) {
            return new ArrayList<>();
        }
        Timestamp start = Timestamp.valueOf(statsParamDto.getStart());
        Timestamp end = Timestamp.valueOf(statsParamDto.getEnd());
        StringBuilder urisBuilder = new StringBuilder();
        for (String uri : statsParamDto.getUris()) {
            urisBuilder.append(uri).append(",");
        }
        urisBuilder.deleteCharAt(urisBuilder.length() - 1);
        List<StatProjection> statOutDtos;
        if (statsParamDto.isUnique()) {
            statOutDtos = hitRepository.countHitsUnique(start, end, urisBuilder.toString());
            log.info("Statistic returned {}", statOutDtos);
            return statOutDtos;
        }
        statOutDtos = hitRepository.countHits(start, end, urisBuilder.toString());
        log.info("Statistic returned {}", statOutDtos);
        return statOutDtos;
    }
}
